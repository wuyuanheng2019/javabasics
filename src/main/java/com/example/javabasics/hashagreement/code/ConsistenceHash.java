package com.example.javabasics.hashagreement.code;

import org.springframework.util.CollectionUtils;

import java.util.*;

public class ConsistenceHash {

    //物理节点集合
    private List<String> realNodes = new ArrayList<>();

    //物理节点与虚拟节点的对应关系，存储的是虚拟节点的hash值
    private Map<String, List<Integer>> real2VirtualMap = new HashMap<>();

    private int virutailNums = 100;

    //排序存储结构 红黑树，key是虚拟节点的hash值，value是物理节点
    private SortedMap<Integer, String> sortedMap = new TreeMap<>();

    public ConsistenceHash(int virutailNums) {
        super();
        this.virutailNums = virutailNums;
    }

    public ConsistenceHash() {
    }

    //加入服务器的方法
    public void addServer(String node) {
        this.realNodes.add(node);
        String vnode = null;
        int i = 0, count = 0;
        List<Integer> virtualNodes = new ArrayList<>();
        this.real2VirtualMap.put(node, virtualNodes);

        //创建虚拟节点，并放在环上(排序存储)
        while (count < this.virutailNums) {
            i++;
            vnode = node + "&&" + i;
            int hashValue = FNV1_32_HASH.getHash(vnode);
            if (!this.sortedMap.containsKey(hashValue)) {
                virtualNodes.add(hashValue);
                this.sortedMap.put(hashValue, node);
                count++;
            }
        }
    }

    //移除一个节点
    public void removeServer(String node) {
        List<Integer> vitualNodes = this.real2VirtualMap.get(node);
        if (!CollectionUtils.isEmpty(vitualNodes)) {
            for (Integer hash : vitualNodes) {
                this.sortedMap.remove(hash);
            }
        }
        this.real2VirtualMap.remove(node);
        this.realNodes.remove(node);
    }

    //找到数据的存放位置
    public String getServer(String key) {
        int hash = FNV1_32_HASH.getHash(key);
        //得到大于该hash值得所有虚拟节点
        SortedMap<Integer, String> subMap = sortedMap.tailMap(hash);

        if (!subMap.isEmpty()) {
            //获取第一个key
            Integer vhash = subMap.firstKey();
            return subMap.get(vhash);
        } else {
            return sortedMap.get(sortedMap.firstKey());
        }
    }

    public static void main(String[] args) {
        ConsistenceHash ch = new ConsistenceHash();
        ch.addServer("192.168.1.10");
        ch.addServer("192.168.1.11");
        ch.addServer("192.168.1.12");

        for (int i = 0; i < 10; i++) {
            System.out.println("a" + i + "对应的服务器：" + ch.getServer("a" + i));
        }
    }
}

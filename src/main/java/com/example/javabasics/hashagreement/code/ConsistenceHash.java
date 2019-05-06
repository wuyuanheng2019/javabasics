package com.example.javabasics.hashagreement.code;

import org.springframework.util.CollectionUtils;

import java.util.*;

public class ConsistenceHash {

    /**
     * 物理节点集合
     */
    private List<String> realNodes = new ArrayList<>();

    /**
     * 物理节点与虚拟节点的对应关系，存储的是虚拟节点的hash值
     */
    private Map<String, List<Integer>> real2VirtualMap = new HashMap<>();

    /**
     * 虚拟节点的个数
     */
    private int virutailNums = 100;

    /**
     * 排序存储结构 红黑树，key是虚拟节点的hash值，value是物理节点（方便找到数据关系：虚拟节点---物理节点）
     * 所以使用了树结构
     */
    private SortedMap<Integer, String> sortedMap = new TreeMap<>();

    /**
     * 指定创建虚拟节点个数
     *
     * @param virutailNums 虚拟节点
     */
    public ConsistenceHash(int virutailNums) {
        super();
        this.virutailNums = virutailNums;
    }

    public ConsistenceHash() {
    }

    /**
     * 加入服务器的方法
     *
     * @param node 服务器节点
     */
    public void addServer(String node) {
        //加入物理节点
        this.realNodes.add(node);
        int i = 0, count = 0;
        //创建虚拟节点，并建立物理节点和虚拟节点的关系
        String vnode = null;
        List<Integer> virtualNodes = new ArrayList<>();
        this.real2VirtualMap.put(node, virtualNodes);

        //创建虚拟节点，并放在环上(排序存储)
        while (count < this.virutailNums) {
            i++;
            vnode = node + "&&" + i;
            //得到对应的hash值（自定义的hash算法，为了得到唯一）
            int hashValue = FNV1_32_HASH.getHash(vnode);
            //判断map中是否包含key
            if (!this.sortedMap.containsKey(hashValue)) {
                //虚拟节点放入map
                virtualNodes.add(hashValue);
                this.sortedMap.put(hashValue, node);
                count++;
            }
        }
    }

    /**
     * 移除一个节点
     *
     * @param node 物理节点
     */
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

    /**
     * 找到数据的存放位置
     *
     * @param key key
     * @return 物理节点
     */
    public String getServer(String key) {
        int hash = FNV1_32_HASH.getHash(key);
        //得到大于该hash值得所有虚拟节点
        SortedMap<Integer, String> subMap = sortedMap.tailMap(hash);

        if (!subMap.isEmpty()) {
            //获取第一个key
            Integer vhash = subMap.firstKey();
            return subMap.get(vhash);
        } else {
            //返回物理节点的位置
            return sortedMap.get(sortedMap.firstKey());
        }
    }

    public static void main(String[] args) {
        ConsistenceHash ch = new ConsistenceHash();
        //放入服务器（也可以是缓存数据库的地址），缓存做集群
        ch.addServer("192.168.1.10");
        ch.addServer("192.168.1.11");
        ch.addServer("192.168.1.12");

        //只是模拟了取出数据的规则，在应用时，需数据放入节点的方法
        for (int i = 0; i < 10; i++) {
            System.out.println("a" + i + "对应的服务器：" + ch.getServer("a" + i));
        }
    }
}

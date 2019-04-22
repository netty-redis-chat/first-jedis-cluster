package com.wangzai.nettywebsocket.redis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 访问redis集群
 * @author aaron.rao
 *
 */
public class RedisCluster
{
    public static void main(String[] args) throws IOException
    {
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort("104.168.141.85", 8001));
        jedisClusterNode.add(new HostAndPort("104.168.141.85", 8002));
        jedisClusterNode.add(new HostAndPort("104.168.141.85", 8003));
        jedisClusterNode.add(new HostAndPort("104.168.141.85", 8004));
        jedisClusterNode.add(new HostAndPort("104.168.141.85", 8005));
        jedisClusterNode.add(new HostAndPort("104.168.141.85", 8006));

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(10);
        config.setTestOnBorrow(true);
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNode, 6000, 10, config);
        System.out.println(jedisCluster.set("student", "aaron4"));
        System.out.println(jedisCluster.set("age", "18"));

        System.out.println(jedisCluster.get("student"));
        System.out.println(jedisCluster.get("age"));

        jedisCluster.close();
    }
}
package trie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trie {
    /*
    根节点
     */
    private final Node root;
    /*
    字典树包含单词个数
     */
    private int size;
    /*
    回溯删除节点的标志位
     */
    private int DeleteFlag;


    public Trie() {
        root = new Node("");
    }

    public int size() {
        return size;
    }

    /**
     * 添加一个单词
     *
     * @param word 单词
     */
    public void add(String word) {
        if (word == null || word.length() == 0)
            throw new RuntimeException("非法参数！");
        Node node = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (node.next.get(c) == null)
                node.next.put(c, new Node(node.value + c));
            node = node.next.get(c);
        }
        if (!node.isWorld) {
            node.isWorld = true;
            ++size;
        }
    }

    /**
     * 查询一个单词是否存在
     *
     * @return true表示该单词存在
     */
    public boolean contains(String word) {
        if (word == null || word.length() == 0)
            throw new RuntimeException("非法参数！");
        Node node = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (node.next.get(c) == null)
                return false;
            node = node.next.get(c);
        }
        return node.isWorld;
    }

    /**
     * 检查trie中前缀prefix是否存在
     *
     * @return true表示存在该前缀
     */
    public boolean hasPrefix(String prefix) {
        if (prefix == null || prefix.length() == 0)
            return false;
        Node node = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (node.next.get(c) == null)
                return false;
            node = node.next.get(c);
        }
        return true;
    }

    /**
     * 以node作为深度优先遍历的起始节点及根节点，遍历以node为根节点的树的所有节点，找到所有单词加入words集合中
     *
     * @param words 保存单词的集合
     * @param node  根节点和遍历起始点
     */
    private void DFS(List<String> words, Node node) {
        if (node == null)
            return;
        if (node.isWorld)
            words.add(node.value);
        /*
        遍历余下所有分支
         */
        if (node.next != null)
            node.next.forEach((k, v) -> DFS(words, v));
    }

    /**
     * 获取匹配前缀字符串的所有单词；
     * 基于这种方法可以实现很多种不同的查询模式（模糊查找，贪婪匹配等等）；
     * 以后有空可以一一实现
     *
     * @param prefix 前缀字符串，只输入"*"表示匹配所有单词
     * @return 所有单词结果的集合
     */
    public List<String> getWordsByPrefix(String prefix) {
        List<String> words = new ArrayList<>();
        if (prefix == null || prefix.length() == 0)
            return words;
        /*
        查询所有
         */
        if (prefix.equals("*")) {
            /*
            深度优先遍历
             */
            DFS(words, root);
            return words;
        }
        Node node = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (node.next.get(c) == null)
                return words;
            node = node.next.get(c);
        }
        DFS(words, node);
        return words;
    }

    /**
     * 深度优先遍历的变体，用以辅助删除trie中的单词和单词对应的分支
     *
     * @return true表示删除成功，未找到该单词会返回false
     */
    private boolean removeByDFS(Node node, String word, int i) {
        if (node == null)//递归中未找到单词的某一个节点，说明该单词不存在，直接结束递归
            return false;
        if (i == word.length()) {//递归到达保存该单词的节点
            if (node.isWorld) {//判断该节点是否有单词
                if (!node.next.isEmpty()) {//该节点下仍有单词
                    node.isWorld = false;//直接置isWorld标志位为false表明已经删除该单词
                    --size;//维护trie大小
                } else {//表明该节点是该分支下最终节点，置DeleteFlag=1表明回溯时清理该分支，删除无用节点
                    DeleteFlag = 1;
                    --size;
                }
                return true;//无论如何都表明已找到该单词，并且要删除该单词
            }
            /*
            该单词没有被保存，置DeleteFlag标志位为0，表示回溯时不需要清理分支
             */
            DeleteFlag = 0;
            return false;
        }
        //if条件成立，进入回溯
        if (removeByDFS(node.next.get(word.charAt(i)), word, i + 1)) {
            if (DeleteFlag == 1) {//是否要清理分支
                /*
                表示该节点分支超过1个，说明从该节点起有其它单词在使用该节点，不能再删除该节点及以上的节点，
                删除最后一个分支节点后,直接置DeleteFlag = 0停止回溯中清理分支操作
                 */
                if (node.next.size() > 1) {
                    node.next.remove(word.charAt(i));
                    DeleteFlag = 0;
                    return true;
                }
                /*
                确保该分支是一个单分支的情况下，仍然可以成功删除该分支
                 */
                if (node.equals(root))
                    node.next.remove(word.charAt(i));
            }
            return true;
        }
        return false;//if条件不成立，单词不存在，直接回溯false
    }

    /**
     * 删除trie中的word单词
     *
     * @return true表示删除成功，false表示单词不存在
     */
    public boolean remove(String word) {
        if (word == null || word.length() == 0)
            throw new RuntimeException("非法参数！");
        DeleteFlag = -1;
        return removeByDFS(root, word, 0);
    }

    /**
     * trie中的节点类
     */
    private static class Node {
        /*
       保存该节点到根节点构成的字符串，
       这么做会极其浪费空间，大部分情况下根本没必要保存，这里我为啥使用？为了方便！
       只要能有高效的时间效率，空间是值得牺牲的，空间是可以扩展的嘛，时间可不等人。。。
         */
        private final String value;
        /*
        是否表示单词
         */
        boolean isWorld;
        /*
        保存分支结点
        HashMap很nice！
         */
        Map<Character, Node> next;

        public Node(String value, boolean isWorld) {
            this.value = value;
            this.isWorld = isWorld;
            this.next = new HashMap<>();
        }

        public Node(String value) {
            this(value, false);
        }
    }
}

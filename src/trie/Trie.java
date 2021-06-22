package trie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trie {

    private final Node root;
    private int size;
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

    private void DFS(List<String> words, Node node) {
        if (node == null)
            return;
        if (node.isWorld)
            words.add(node.value);
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

    private boolean removeByDFS(Node node, String word, int i) {
        if (node == null)
            return false;
        if (i == word.length()) {
            if (node.isWorld) {
                if (!node.next.isEmpty()) {
                    node.isWorld = false;
                    --size;
                } else DeleteFlag = 1;
                return true;
            }
            DeleteFlag = 0;
            return false;
        }
        if (DeleteFlag == -1)
            if (removeByDFS(node.next.get(word.charAt(i)), word, i + 1)) {
                if (DeleteFlag == 1)
                    if (node.next.size() == 1)
                        node.next.clear();
                    else{
                        node.next.remove(word.charAt(i));
                        DeleteFlag = 0;
                    }
                return true;
            }
        return false;
    }

    public boolean remove(String word) {
        if (word == null || word.length() == 0)
            throw new RuntimeException("非法参数！");
        DeleteFlag = -1;
        return removeByDFS(root, word, 0);
    }

    private static class Node {
        private final String value;
        boolean isWorld;
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

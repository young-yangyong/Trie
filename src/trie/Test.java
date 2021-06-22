package trie;


public class Test {
    public static void main(String[] args) {
        final Trie trie = new Trie();
        trie.add("abc");
        trie.add("acd");
        trie.add("bdf");
        trie.add("abcde");
        trie.add("abcdef");
        trie.add("abcfr");
        trie.add("abcbe");
        System.out.println(trie.size());
        System.out.println(trie.contains("abcd"));
        System.out.println(trie.hasPrefix("abc"));
        System.out.println(trie.getWordsByPrefix("*"));
        System.out.println(trie.getWordsByPrefix("b"));
        System.out.println(trie.getWordsByPrefix("B"));
        System.out.println(trie.getWordsByPrefix("a"));
        System.out.println(trie.remove("abcde"));
        System.out.println(trie.size());
        System.out.println(trie.getWordsByPrefix("*"));
        System.out.println(trie.remove("bdf"));
        System.out.println(trie.getWordsByPrefix("*"));
    }
}

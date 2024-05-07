// **********************************************************************************
// Title: Music Organizer
// Author: Angel Diaz
// Course Section: CMIS201-ONL1 (Seidel) Spring 2024
// File: BST.java
// Description: Fulfills the BST implementation for my program. It allows the user to search for songs via title.
// **********************************************************************************
package com.angeldiaz.musicorganizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BST {
    private Node root;

    private class Node {
        HashMap<String, String> data;
        Node left;
        Node right;

        public Node(HashMap<String, String> data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }

    public BST() {
        this.root = null;
    }

    public void insert(HashMap<String, String> data) {
        root = insertRecursive(root, data);
    }

    private Node insertRecursive(Node root, HashMap<String, String> data) {
        if (root == null) {
            root = new Node(data);
            return root;
        }

        String title = data.get("Title");
        String currentNodeTitle = root.data.get("Title");
        if (title.compareToIgnoreCase(currentNodeTitle) < 0) {
            root.left = insertRecursive(root.left, data);
        } else if (title.compareToIgnoreCase(currentNodeTitle) > 0) {
            root.right = insertRecursive(root.right, data);
        }

        return root;
    }

    public List<HashMap<String, String>> searchAll(String title) {
        List<HashMap<String, String>> results = new ArrayList<>();
        if (root != null) {
            searchAllRecursive(root, title, results);
        }
        return results;
    }

    private void searchAllRecursive(Node root, String searchQuery, List<HashMap<String, String>> results) {
        if (root != null) {
            String nodeTitle = root.data.get("Title");
            if (nodeTitle != null && nodeTitle.toLowerCase().contains(searchQuery.toLowerCase())) {
                results.add(root.data);
            }

            searchAllRecursive(root.left, searchQuery, results);
            searchAllRecursive(root.right, searchQuery, results);
        }
    }
}
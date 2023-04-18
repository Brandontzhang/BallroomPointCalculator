package com.o2cmparser.filters;

import org.jsoup.select.Elements;

import com.o2cmparser.filters.FilterMethods.FilterMethod;

/**
 * Custom traveral of HTMl elements
 * Unlike the NodeTraversor included in JSOUP, this one only returns the leaf nodes
 */
public class NodeFilter {

  public FilterMethod filter;

  public NodeFilter(FilterMethod filter) {
    this.filter = filter;
  }

  public void traverse(Elements elements) {

  }
}

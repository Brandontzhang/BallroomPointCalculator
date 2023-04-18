package com.o2cmparser.filters;

import org.jsoup.select.Elements;

import com.o2cmparser.filters.FilterMethods.FilterMethod;

public class ElementsFilter {

  public Elements elements;

  public FilterMethod filter;

  public ElementsFilter(Elements elements, FilterMethod filter) {
    this.elements = elements;
    this.filter = filter;
  }

  public Elements filter() {
    NodeFilter nodeFilter = new NodeFilter(this.filter);
    nodeFilter.traverse(elements);
    return this.elements;
  }
}

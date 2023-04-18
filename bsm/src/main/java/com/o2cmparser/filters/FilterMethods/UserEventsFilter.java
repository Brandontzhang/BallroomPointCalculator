package com.o2cmparser.filters.FilterMethods;

import org.jsoup.nodes.Node;
import org.jsoup.select.NodeFilter;

public class UserEventsFilter implements NodeFilter {
  /**
   * Depth represents the number of html tags we've traversed
   * At each level, there are conditons we check to see if we want to continue looking into the set of tags.
   * If at a top level we find that we don't need to look any further, we skip looking at the children
   */
  @Override
  public FilterResult head(Node node, int depth) {
    if (depth == 1) {
      if (node.childNodeSize() == 0) {
        return NodeFilter.FilterResult.SKIP_CHILDREN;
      } else if (node.hasAttr("class") && node.attr("class").equals("t1n")) {
        return NodeFilter.FilterResult.CONTINUE;
      } else {
        return NodeFilter.FilterResult.REMOVE;
      }
    }

    if (depth == 2) {
      if (node.childNodeSize() == 0) {
        return NodeFilter.FilterResult.REMOVE;
      }
      if (!node.hasAttr("href")) {
        return NodeFilter.FilterResult.REMOVE;
      }
    }

    return NodeFilter.FilterResult.CONTINUE;
  }
}

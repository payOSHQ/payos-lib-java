package vn.payos.core;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** AutoPager */
public class AutoPager<Item> implements Iterable<Item> {
  private final Page<Item> initialPage;

  /**
   * AutoPager
   *
   * @param initialPage initial page
   */
  public AutoPager(Page<Item> initialPage) {
    this.initialPage = initialPage;
  }

  @Override
  public Iterator<Item> iterator() {
    return new AutoPagerIterator();
  }

  private class AutoPagerIterator implements Iterator<Item> {
    private Page<Item> currentPage = initialPage;
    private Iterator<Item> currentIterator = currentPage.getItems().iterator();

    @Override
    public boolean hasNext() {
      if (currentIterator.hasNext()) {
        return true;
      }
      while (!currentIterator.hasNext() && currentPage.hasNextPage()) {
        currentPage = currentPage.nextPage();
        currentIterator = currentPage.getItems().iterator();
      }
      return currentIterator.hasNext();
    }

    @Override
    public Item next() {
      if (!hasNext()) {
        throw new NoSuchElementException("No more elements");
      }
      return currentIterator.next();
    }
  }

  /**
   * Stream
   *
   * @return Stream
   */
  public Stream<Item> stream() {
    return StreamSupport.stream(spliterator(), false);
  }
}

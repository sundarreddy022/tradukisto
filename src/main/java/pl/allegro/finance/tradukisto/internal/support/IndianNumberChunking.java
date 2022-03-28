package pl.allegro.finance.tradukisto.internal.support;

import com.google.common.math.IntMath;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class IndianNumberChunking {

  private static final Integer LESS_THAN_THOUSAND_CHUNK_SIZE = 3;
  private static final Integer GREATER_THAN_THOUSAND_CHUNK_SIZE = 2;

  public List<Integer> chunk(Integer value) {
    Deque<Integer> result = new ArrayDeque<>();

    // Only once make a chunk of 3 for the first digits
    if (value > 0) {
      result.addFirst(value % getSplitFactor(LESS_THAN_THOUSAND_CHUNK_SIZE));
      value /= getSplitFactor(LESS_THAN_THOUSAND_CHUNK_SIZE);
    }

    //Rest of the value, make chunks of 2
    while (value > 0) {
      result.addFirst(value % getSplitFactor(GREATER_THAN_THOUSAND_CHUNK_SIZE));
      value /= getSplitFactor(GREATER_THAN_THOUSAND_CHUNK_SIZE);
    }

    return new ArrayList<>(result);
  }

  private Integer getSplitFactor(Integer chunkSize) {
    return IntMath.pow(10, chunkSize);
  }
}

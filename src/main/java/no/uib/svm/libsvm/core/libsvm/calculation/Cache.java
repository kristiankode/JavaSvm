package no.uib.svm.libsvm.core.libsvm.calculation;

/**
 * Created by Markus on 04.09.2015.
 */
public class Cache {

    private final int length;
    private long size;

    /**
     * A circular list.
     * Data[0, length] is cached in this entry
     */
    private final class HeadList {
        HeadList prev, next;
        float[] data;
        int length;
    }
    private final HeadList[] head;
    private HeadList lruHead;

    Cache(int length, int size){
        this.size = size;
        this.length = length;
        head = new HeadList[length];
        for(int i = 0; i < length; ++i){
            head[i] = new HeadList();
        }
        size /= 4;
        size -= 1 * (16/4); //sizeof(HeadList) == 16
        size = (int) Math.max(size, 2 * (long) length); // Cache must be large enough for two columns
        lruHead = new HeadList();
        lruHead.next = lruHead.prev = lruHead;
    }

    /**
     * Delete from current location
     *
     * @param h
     */
    private void lruDelete(HeadList h){
        h.prev.next = h.next;
        h.next.prev = h.prev;
    }

    /**
     * Insert to last position
     *
     * @param h
     */
    private void lruInsert(HeadList h){
        h.next = lruHead;
        h.prev = lruHead.prev;
        h.prev.next = h;
        h.next.prev = h;
    }

    /**
     * request data [0, len]
     *
     * return some position p here [p, len] need to be filled
     * (p >= len if nothing need to be filled)
     * Java: Simulate pointer using single-element array
     *
     * @param index
     * @param data
     * @param len
     * @return
     */
    protected int getData(int index, float[][] data, int len){
        HeadList h = head[index];

        if(h.length > 0){
            lruDelete(h);
        }

        int more = len - h.length;

        if(more > 0) {

            // free old space
            while (size < more) {
                HeadList old = lruHead.next;
                lruDelete(old);
                size += old.length;
                old.data = null;
                old.length = 0;
            }

            // allocate new space
            float[] newData = new float[len];
            if (h.data != null)
                System.arraycopy(h.data, 0, newData, 0, h.length);
            h.data = newData;
            size -= more;

            {
                int _ = h.length;
                h.length = len;
                len = _;
            }
        }
            lruInsert(h);
            data[0] = h.data;
            return len;
    }

    protected void swapIndex(int i, int j){
        if(i == j) return;
        if(head[i].length > 0) lruDelete(head[i]);
        if(head[j].length > 0) lruDelete(head[j]);
        { // new Scope
            float[] _ = head[i].data;
            head[i].data = head[j].data;
            head[j].data = _;
        }
        // new Scope
        {
            int _ = head[i].length;
            head[i].length = head[j].length;
            head[j].length = _;
        }
        if(head[i].length > 0) lruInsert(head[i]);
        if(head[j].length > 0) lruInsert(head[j]);

        if(i > j){
            {
                int _ = i;
                i = j;
                j = _;
            }
        }
        for(HeadList h = lruHead.next; h != lruHead; h=h.next){

            if(h.length > i){

                if(h.length > j){
                    {
                        float _ = h.data[i];
                        h.data[i] = h.data[j];
                        h.data[j] = _;
                    }
                }else {
                    // give up
                    lruDelete(h);
                    size += h.length;
                    h.data = null;
                    h.length = 0;
                }
            }
        }
    }


}

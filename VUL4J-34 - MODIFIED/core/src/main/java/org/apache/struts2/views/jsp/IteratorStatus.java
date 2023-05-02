

package org.apache.struts2.views.jsp;



public class IteratorStatus {
    protected StatusState state;

    public IteratorStatus(StatusState aState) {
        state = aState;
    }

    public int getCount() {
        return state.index + 1;
    }

    public boolean isEven() {
        return ((state.index + 1) % 2) == 0;
    }

    public boolean isFirst() {
        return state.index == 0;
    }

    public int getIndex() {
        return state.index;
    }

    public boolean isLast() {
        return state.last;
    }

    public boolean isOdd() {
        return ((state.index + 1) % 2) != 0;
    }

    public int modulus(int operand) {
        return (state.index + 1) % operand;
    }

    public static class StatusState {
        boolean last = false;
        int index = 0;

        public void setLast(boolean isLast) {
            last = isLast;
        }

        public void next() {
            index++;
        }
    }
}

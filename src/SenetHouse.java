    public enum SenetHouse {
    NEW_BEGINNING(14), // مربع 15
    ROADBLOCK(25),  // مربع 26
    RETURN_BOX(26),      // مربع 27
    THREE_BOX(27),      // مربع 28
    TWO_BOX(28),   // مربع 29
    FREE_BOX(29);      // مربع 30
          

    private final int index;

    SenetHouse(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
    }

package src;


class Tetrimino {
    //each letter stands for a type of tetrimino block
    private static final String[] TYPE = new String[] {"o","i","t","l","j","s","z"};

    private int[][] blocks = new int[4][2];
    private int anchorLeft;
    private int anchorRight;
    // S and Z anchors
    private int anchorTurn;
    private int temp;
    private int type;
    private int facing;

    //coonstructors based on if a string or int is passed
    public Tetrimino(int type){
        changeType(type);
    }

    public Tetrimino(String type){
        changeType(type);
    }

    
    public void changeType(int type){
        facing = 0;

        if (type == 1){
            blocks[0] = new int[] {-1,0};
            blocks[1] = new int[] {0,0};
            blocks[2] = new int[] {-1,-1};
            blocks[3] = new int[] {0,-1};
            anchorLeft = 0;
            anchorRight = 1;
            anchorTurn = 1;
            this.type = type;
        }
        else if (type == 2){
            blocks[0] = new int[] {-2,0};
            blocks[1] = new int[] {-1,0};
            blocks[2] = new int[] {0,0};
            blocks[3] = new int[] {1,0};
            anchorLeft = 1;
            anchorRight = 2;
            anchorTurn = 2;
            this.type = type;
        }
        else if (type == 3){
            blocks[0] = new int[] {0,1};
            blocks[1] = new int[] {-1,0};
            blocks[2] = new int[] {0,0};
            blocks[3] = new int[] {1,0};
            anchorLeft = 3;
            anchorRight = 1;
            anchorTurn = 2;
            this.type = type;
        }
        else if (type == 4){
            blocks[0] = new int[] {1,1};
            blocks[1] = new int[] {-1,0};
            blocks[2] = new int[] {0,0};
            blocks[3] = new int[] {1,0};
            anchorLeft = 2;
            anchorRight = 1;
            anchorTurn = 2;
            this.type = type;
        }
        else if (type == 5){
            blocks[0] = new int[] {-1,1};
            blocks[1] = new int[] {-1,0};
            blocks[2] = new int[] {0,0};
            blocks[3] = new int[] {1,0};
            anchorLeft = 3;
            anchorRight = 2;
            anchorTurn = 2;
            this.type = type;
        }
        else if (type == 6){
            blocks[0] = new int[] {0,0};
            blocks[1] = new int[] {1,0};
            blocks[2] = new int[] {-1,-1};
            blocks[3] = new int[] {0,-1};
            anchorLeft = 0;
            anchorRight = 0;
            anchorTurn = 0;
            this.type = type;
        }
        else if (type == 7){
            blocks[0] = new int[] {-1,0};
            blocks[1] = new int[] {0,0};
            blocks[2] = new int[] {0,-1};
            blocks[3] = new int[] {1,-1};
            anchorLeft = 1;
            anchorRight = 1;
            anchorTurn = 1;
            this.type = type;
        }
        else {
            throw new IllegalArgumentException("invalid piece number, valid pieces numbers are [1,2,3,4,5,6,7]");
        }
    }

    //method changeType(String type)
    //changes the type from string to int
    public void changeType(String type){
        facing = 0;

        if (type.equals("o")){
            this.type = 1;
        }
        else if (type.equals("i")){
            this.type = 2;
        }
        else if (type.equals("t")){
            this.type = 3;
        }
        else if (type.equals("l")){
            this.type = 4;
        }
        else if (type.equals("j")){
            this.type = 5;
        }
        else if (type.equals("s")){
            this.type = 6;
        }
        else if (type.equals("z")){
            this.type = 7;
        }
        else {
            throw new IllegalArgumentException("invalid piece type, valid pieces are [o,i,t,l,j,s,z]");
        }

        changeType(this.type);
    }

    //method rotate
    //used to rotate the blocks based on direction by coordinating them on the imaginary board grid
    public void rotate(String direction){
        if (direction.equals("left")){
            for (int i = 0; i < 4; i++){
                temp = blocks[i][0];
                blocks[i][0] = -blocks[i][1];
                blocks[i][1] = temp;
            }

            facing = (facing - 1) % 4;
        }
        else if (direction.equals("right")){
            for (int i = 0; i < 4; i++){
                temp = blocks[i][1];
                blocks[i][1] = -blocks[i][0];
                blocks[i][0] = temp;
            }

            facing = (facing + 1) % 4;
        }
        else if (direction.equals("turn")){
            for (int i = 0; i < 4; i++){
                blocks[i][0] = -blocks[i][0];
                blocks[i][1] = -blocks[i][1];
            }

            facing = (facing + 2) % 4;
        }
    }

    public int[] block(int blockIndex){
        return blocks[blockIndex];
    }

    public String typeString(){
        return TYPE[type - 1];
    }

    public int typeInt(){
        return type;
    }    

    //method anchorIndex
    //returns either anchorLeft, anchorRight, or anchorTurn based on what direction
    //the block should turn towards
    public int anchorIndex(String direction){
        if (direction.equals("left")){
            return anchorLeft;
        }
        else if (direction.equals("right")){
            return anchorRight;
        }
        else if (direction.equals("turn")){
            return anchorTurn;
        }
        else {
            throw new IllegalArgumentException("invalid rotation direction");
        }
    }

    //method facing
    //returns the value of the variable facing
    public int facing(){
        return facing;
    }
}
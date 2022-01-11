// import java.util.Random;


class Tetrimino {
    private static final String[] TYPE = new String[] {"o","i","t","l","j","s","z"};

    private int[][] blocks = new int[4][2];
    private int anchorLeft;
    private int anchorRight;
    private int anchorTurn;
    private int temp;
    private int type;

    public Tetrimino(String type){
        changeType(type);
    }

    public Tetrimino(int type){
        changeType(type);
    }

    public void changeType(String type){
        if (type.equals("o")){
            blocks[0] = new int[] {-1,0};
            blocks[1] = new int[] {0,0};
            blocks[2] = new int[] {-1,-1};
            blocks[3] = new int[] {0,-1};
            anchorLeft = 0;
            anchorRight = 1;
            anchorTurn = 1;
            this.type = 1;
        }
        else if (type.equals("i")){
            blocks[0] = new int[] {-2,0};
            blocks[1] = new int[] {-1,0};
            blocks[2] = new int[] {0,0};
            blocks[3] = new int[] {1,0};
            anchorLeft = 1;
            anchorRight = 2;
            anchorTurn = 2;
            this.type = 2;
        }
        else if (type.equals("t")){
            blocks[0] = new int[] {0,1};
            blocks[1] = new int[] {-1,0};
            blocks[2] = new int[] {0,0};
            blocks[3] = new int[] {1,0};
            anchorLeft = 3;
            anchorRight = 1;
            anchorTurn = 2;
            this.type = 3;
        }
        else if (type.equals("l")){
            blocks[0] = new int[] {1,1};
            blocks[1] = new int[] {-1,0};
            blocks[2] = new int[] {0,0};
            blocks[3] = new int[] {1,0};
            anchorLeft = 2;
            anchorRight = 2;
            anchorTurn = 2;
            this.type = 4;
        }
        else if (type.equals("j")){
            blocks[0] = new int[] {-1,1};
            blocks[1] = new int[] {-1,0};
            blocks[2] = new int[] {0,0};
            blocks[3] = new int[] {1,0};
            anchorLeft = 2;
            anchorRight = 2;
            anchorTurn = 2;
            this.type = 5;
        }
        else if (type.equals("s")){
            blocks[0] = new int[] {0,0};
            blocks[1] = new int[] {1,0};
            blocks[2] = new int[] {-1,-1};
            blocks[3] = new int[] {0,-1};
            anchorLeft = 0;
            anchorRight = 0;
            anchorTurn = 0;
            this.type = 6;
        }
        else if (type.equals("z")){
            blocks[0] = new int[] {0,0};
            blocks[1] = new int[] {1,0};
            blocks[2] = new int[] {1,-1};
            blocks[3] = new int[] {2,-1};
            anchorLeft = 1;
            anchorRight = 1;
            anchorTurn = 1;
            this.type = 7;
        }
        else {
            throw new IllegalArgumentException("invalid piece type, valid pieces are [o,i,t,l,j,s,z]");
        }
    }

    public void changeType(int type){
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
            anchorRight = 2;
            anchorTurn = 2;
            this.type = type;
        }
        else if (type == 5){
            blocks[0] = new int[] {-1,1};
            blocks[1] = new int[] {-1,0};
            blocks[2] = new int[] {0,0};
            blocks[3] = new int[] {1,0};
            anchorLeft = 2;
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
            blocks[0] = new int[] {0,0};
            blocks[1] = new int[] {1,0};
            blocks[2] = new int[] {1,-1};
            blocks[3] = new int[] {2,-1};
            anchorLeft = 1;
            anchorRight = 1;
            anchorTurn = 1;
            this.type = type;
        }
        else {
            throw new IllegalArgumentException("invalid piece number, valid pieces numbers are [1,2,3,4,5,6,7]");
        }
    }

    public void rotate(String direction){
        if (direction.equals("left")){
            for (int i = 0; i < 4; i++){
                temp = blocks[i][0];
                blocks[i][0] = -blocks[i][1];
                blocks[i][1] = temp;
            }
        }
        else if (direction.equals("right")){
            for (int i = 0; i < 4; i++){
                temp = blocks[i][1];
                blocks[i][1] = -blocks[i][0];
                blocks[i][0] = temp;
            }
        }
        else if (direction.equals("turn")){
            for (int i = 0; i < 4; i++){
                blocks[i][0] = -blocks[i][0];
                blocks[i][1] = -blocks[i][1];
            }
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

    // TODO this is a debug function, delete later
    public void print(){
        boolean o = false;
        for (int y = 2; y > -3; y--){
            for (int x = -2; x < 3; x++){
                o = false;

                for (int i = 0; i < 4; i++){
                    if (blocks[i][0] == x && blocks[i][1] == y){
                        if (i == anchorTurn){
                            System.out.print("a");
                        }
                        else {
                            System.out.print("x");
                        }
                        o = true;
                    }
                }

                if (!o){
                    System.out.print(" ");
                }
            }
            System.out.print("\n");
        }
    }
}
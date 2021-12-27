// import java.util.Random;


class Tetrimino {
    private int[][] blocks;
    private int anchorLeft;
    private int anchorRight;
    private int anchorTurn;

    public Tetrimino(String type){
        changeType(type);
    }

    public void changeType(String type){
        if (type.equals("o")){
            blocks[1] = new int[] {-1,0};
            blocks[2] = new int[] {0,0};
            blocks[3] = new int[] {-1,-1};
            blocks[4] = new int[] {0,-1};
            anchorLeft = 1;
            anchorRight = 2;
            anchorTurn = 2;
        }
        else if (type.equals("i")){
            blocks[1] = new int[] {-2,0};
            blocks[2] = new int[] {-1,0};
            blocks[3] = new int[] {0,0};
            blocks[4] = new int[] {1,0};
            anchorLeft = 2;
            anchorRight = 3;
            anchorTurn = 3;
        }
        else if (type.equals("t")){
            blocks[1] = new int[] {0,1};
            blocks[2] = new int[] {-1,0};
            blocks[3] = new int[] {0,0};
            blocks[4] = new int[] {1,0};
            anchorLeft = 4;
            anchorRight = 2;
            anchorTurn = 3;
        }
        else if (type.equals("l")){
            blocks[1] = new int[] {1,1};
            blocks[2] = new int[] {-1,0};
            blocks[3] = new int[] {0,0};
            blocks[4] = new int[] {1,0};
            anchorLeft = 3;
            anchorRight = 3;
            anchorTurn = 3;
        }
        else if (type.equals("j")){
            blocks[1] = new int[] {-1,1};
            blocks[2] = new int[] {-1,0};
            blocks[3] = new int[] {0,0};
            blocks[4] = new int[] {1,0};
            anchorLeft = 3;
            anchorRight = 3;
            anchorTurn = 3;
        }
        else if (type.equals("s")){
            blocks[1] = new int[] {0,0};
            blocks[2] = new int[] {1,0};
            blocks[3] = new int[] {-1,-1};
            blocks[4] = new int[] {0,-1};
            anchorLeft = 1;
            anchorRight = 1;
            anchorTurn = 1;
        }
        else if (type.equals("z")){
            blocks[1] = new int[] {0,0};
            blocks[2] = new int[] {1,0};
            blocks[3] = new int[] {1,-1};
            blocks[4] = new int[] {2,-1};
            anchorLeft = 2;
            anchorRight = 2;
            anchorTurn = 2;
        }
        else {
            throw new java.lang.Error("invalid piece type, valid pieces are [o,i,t,l,j,s,z]");
        }
    }

    public int[] block(int blockIndex){
        return blocks[blockIndex];
    }

    public int anchorLeftIndex(){
        return anchorLeft;
    }

    public int anchorRightIndex(){
        return anchorRight;
    }

    public int anchorTurnIndex(){
        return anchorTurn;
    }
}
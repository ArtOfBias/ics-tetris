package src;


public class Bag {
    private int[] bagPieces = new int[] {1,2,3,4,5,6,7};

    public Bag(){
        shuffle();
    }

    public int piece(int index){
        return bagPieces[index];
    }

    public void shuffle(){
        int targetIndex;
        int temp;

        for (int i = 0; i < 7; i++){
            targetIndex = randInt(0, 6);

            temp = bagPieces[i];
            bagPieces[i] = bagPieces[targetIndex];
            bagPieces[targetIndex] = temp;
        }
    }

    // TODO not sure if this is useful
    public void reset(){
        bagPieces = new int[] {1,2,3,4,5,6,7};
    }

    private static int randInt(int a, int b){
        return (int)Math.floor(randNumber(a, b + 1));
    }

    private static double randNumber(double a, double b){
        double x = Math.random();
        return x * (b - a) + a;
    }
}

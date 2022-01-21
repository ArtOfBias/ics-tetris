package src;


public class Bag {
    //Declaration of array
    private int[] bagPieces = new int[] {1,2,3,4,5,6,7};

    //constructor calls on shuffle()
    public Bag(){
        shuffle();
    }

    //method piece()
    //returns the piece number
    //pre: number from 0-6
    //post: number from 1-7 
    public int piece(int index){
        return bagPieces[index];
    }

    //method shuffle()
    //shuffles the bag so that the pieces are randomized
    //post: shuffles the bag (the array)
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

    //method randInt()
    //returns a random int
    //pre: takes two integers a and b
    //post: returns a random integer between a to b
    private static int randInt(int a, int b){
        return (int)Math.floor(randNumber(a, b + 1));
    }

    //method randInt()
    //returns a random number
    //pre: takes two doubles a and b
    //post: returns a random number between a to b
    private static double randNumber(double a, double b){
        double x = Math.random();
        return x * (b - a) + a;
    }
}

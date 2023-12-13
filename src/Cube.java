/* Alex Rodriguez
 * Abhigyan Tripathi
 * 
 * Cube Class
 */


import java.util.*;


public class Cube {
    private Deque<Cube>[] mystack;
    private int[][] front;
    private int[][] up;
    private int[][] down;
    private int[][] back;
    private int[][] left;
    private int[][] right;
    private String face;
    private int level;
    private int cost;
    private int[] faceCost = new int[6];
    private RubiksSolver solver;

    // Constructor that initializes the Cube with matrices for each face and other properties
    public Cube(RubiksSolver solver) {
        // Initialize the 3x3 matrices for each face of the cube
        front = new int[3][3];
        up = new int[3][3];
        down = new int[3][3];
        back = new int[3][3];
        left = new int[3][3];
        right = new int[3][3];

        // Initialize faceCost array
        faceCost = new int[6];

        this.solver = solver;

    }
     // Copy constructor to create a copy of the Cube
     public Cube(Cube other) {
        this.front = copyFace(other.front);
        this.up = copyFace(other.up);
        this.down = copyFace(other.down);
        this.back = copyFace(other.back);
        this.left = copyFace(other.left);
        this.right = copyFace(other.right);
        
        this.face = other.face;
        // Copy any other relevant properties
    
        // Copy or reference the RubiksSolver, depending on your application logic
        this.solver = other.solver;
    }

    // Public getter for the 'face' field
    public String getFace() {
        return face;
    }

    // Public setter for the 'face' field
    public void setFace(String face) {
        this.face = face;
    }

    // Public getter for the 'cost' field
    public int getCost() {
        return cost;
    }

    // Public setter for the 'cost' field
    public void setCost(int cost) {
        this.cost = cost;
    }

    // Public getter for the 'level' field
    public int getLevel() {
        return level;
    }

    // Public setter for the 'level' field
    public void setLevel(int level) {
        this.level = level;
    }

    // Getter for faceCost
    public int[] getFaceCost() {
        return faceCost;
    }

    // Setter for faceCost if needed
    public void setFaceCost(int[] faceCost) {
        if (faceCost != null && faceCost.length == 6) {
            this.faceCost = faceCost;
        }
    }

    // Setters for individual faces of the Cube

    public void setUp(int[][] up) {
        this.up = up;
    }

    public void setFront(int[][] front) {
        this.front = front;
    }

    public void setBack(int[][] back) {
        this.back = back;
    }

    public void setRight(int[][] right) {
        this.right = right;
    }

    public void setLeft(int[][] left) {
        this.left = left;
    }

    public void setDown(int[][] down) {
        this.down = down;
    }


    // Utility method to copy a face matrix
    private int[][] copyFace(int[][] face) {
        int[][] newFace = new int[face.length][face[0].length];
        for (int i = 0; i < face.length; i++) {
            System.arraycopy(face[i], 0, newFace[i], 0, face[i].length);
        }
        return newFace;
    }

    // Computes the cost of the Cube based on face mismatches
    public int computeCost() {
        int cost = 0;
        int center;
    
        center = this.up[1][1];
        this.faceCost[0] = countMismatches(this.up, center);
        cost += this.faceCost[0];
    
        center = this.front[1][1];
        this.faceCost[1] = countMismatches(this.front, center);
        cost += this.faceCost[1];
    
        center = this.left[1][1];
        this.faceCost[2] = countMismatches(this.left, center);
        cost += this.faceCost[2];
    
        center = this.right[1][1];
        this.faceCost[3] = countMismatches(this.right, center);
        cost += this.faceCost[3];
    
        center = this.back[1][1];
        this.faceCost[4] = countMismatches(this.back, center);
        cost += this.faceCost[4];
    
        center = this.down[1][1];
        this.faceCost[5] = countMismatches(this.down, center);
        cost += this.faceCost[5];
    
        Arrays.sort(this.faceCost);
        return cost;
    }
    
    // Utility method to count the number of mismatches in a face
    private int countMismatches(int[][] face, int centerValue) {
        int mismatches = 0;
        for (int i = 0; i < face.length; i++) {
            for (int j = 0; j < face[i].length; j++) {
                if (face[i][j] != centerValue) {
                    mismatches++;
                }
            }
        }
        return mismatches;
    }
    
    // Prints the configuration of the Cube faces
    public void printCube() {

        System.out.println("----------------------Print Cube------------------------------------------------");
        System.out.println("Up");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(this.up[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("\nDown");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(this.down[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("\nFront");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(this.front[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("\nBack");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(this.back[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("\nright");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(this.right[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("\nleft");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(this.left[i][j] + " ");
            }
            System.out.println();
        }


        System.out.println("\nPath: " + this.face);
    }

    private static int countF2 = 0;
    
    public void F2(int mode) {
        transposeInPlace(this.front);
        swapColumns(this.front, 0, 2);

        for (int i = 0; i < 3; i++) {
            int temp = this.up[2][i];
            this.up[2][i] = this.right[i][0];
            this.right[i][0] = this.left[2 - i][2];
            this.left[2 - i][2] = this.down[0][2 - i];
            this.down[0][2 - i] = temp;
        }

        transposeInPlace(this.front);
        swapColumns(this.front, 0, 2);

        // The rest of the operations
        countF2++;

        this.face += "F2";

        // Assuming pushIn is a method in another class
        this.solver.pushIn(this, mode);
    }

    // Utility methods for face manipulation (transpose, swapColumns, swapRows, reverseInPlace)

    private void transposeInPlace(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i; j < matrix[0].length; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
    }

    private void swapColumns(int[][] matrix, int col1, int col2) {
        for (int i = 0; i < matrix.length; i++) {
            int temp = matrix[i][col1];
            matrix[i][col1] = matrix[i][col2];
            matrix[i][col2] = temp;
        }
    }

    // Rotation operations on the Cube's faces (F, F2, Fdash, orientL, orientR, orientU, orientD, orientB)

    private static int countF = 0;

    public void F(int mode) {
        transposeInPlace(this.front);
        swapColumns(this.front, 0, 2);

        for (int i = 0; i < 3; i++) {
            int temp = this.up[2][i];
            this.up[2][i] = this.right[i][0];
            this.right[i][0] = this.left[2 - i][2];
            this.left[2 - i][2] = this.down[0][2 - i];
            this.down[0][2 - i] = temp;
        }

        countF++;

        this.face += "F1";

        // Assuming RubiksSolver has an instance method pushIn and solver is a RubiksSolver instance
        solver.pushIn(this, mode);
    }

    private static int countFDash = 0;

    public void Fdash(int mode) {
        transposeInPlace(this.front);
        swapRows(this.front, 0, 2);

        for (int i = 0; i < 3; i++) {
            int temp = this.up[2][i];
            this.up[2][i] = this.left[2 - i][2];
            this.left[2 - i][2] = this.down[0][2 - i];
            this.down[0][2 - i] = this.right[i][0];
            this.right[i][0] = temp;
        }

        countFDash++;

        this.face += "F3";

        
        solver.pushIn(this, mode);
    }

    private void swapRows(int[][] matrix, int row1, int row2) {
        for (int i = 0; i < matrix[0].length; i++) {
            int temp = matrix[row1][i];
            matrix[row1][i] = matrix[row2][i];
            matrix[row2][i] = temp;
        }
    }

    public void orientL(int mode) {
        transposeInPlace(this.up);
        swapRows(this.up, 0, 2);

        transposeInPlace(this.down);
        swapColumns(this.down, 0, 2);

        int[][] temp = this.front;
        this.front = this.left;
        this.left = this.back;
        this.back = this.right;
        this.right = temp;

        this.face += "-L-";

        // Assuming allMoves is a method in RubiksSolver and solver is a RubiksSolver instance
        this.allMoves(mode);
    }

    public void orientR(int mode) {
        transposeInPlace(this.up);
        swapColumns(this.up, 0, 2);

        transposeInPlace(this.down);
        swapRows(this.down, 0, 2);

        int[][] temp = this.front;
        this.front = this.right;
        this.right = this.back;
        this.back = this.left;
        this.left = temp;

        this.face += "-R-";

        // Assuming allMoves is a method in RubiksSolver and solver is a RubiksSolver instance
        this.allMoves(mode);
    }

    public void orientU(int mode) {
        transposeInPlace(this.left);
        swapColumns(this.left, 0, 2);

        transposeInPlace(this.right);
        swapRows(this.right, 0, 2);

        int[][] temp = this.front;
        this.front = this.up;
        this.up = this.back;
        this.back = this.down;
        this.down = temp;

        reverseInPlace(this.up);
        reverseInPlace(this.back);

        this.face += "-U-";

        // Assuming allMoves is a method in RubiksSolver and solver is a RubiksSolver instance
        this.allMoves(mode);
    }

    private void reverseInPlace(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            reverseArray(matrix[i]);
        }
    }

    private void reverseArray(int[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    public void orientD(int mode) {
        transposeInPlace(this.right);
        swapColumns(this.right, 0, 2);

        transposeInPlace(this.left);
        swapRows(this.left, 0, 2);

        int[][] temp = this.front;
        this.front = this.down;
        this.down = this.back;
        this.back = this.up;
        this.up = temp;

        swapRows(this.down, 0, 2);
        swapColumns(this.down, 0, 2);
        reverseInPlace(this.back);

        this.face += "-D-";

        // Assuming allMoves is a method in RubiksSolver and solver is a RubiksSolver instance
        this.allMoves(mode);
    }

    public void orientB(int mode) {
        for (int i = 0; i < 2; i++) { // Repeat the process twice
            transposeInPlace(this.up);
            swapColumns(this.up, 0, 2);

            transposeInPlace(this.down);
            swapRows(this.down, 0, 2);

            int[][] temp = this.front;
            this.front = this.right;
            this.right = this.back;
            this.back = this.left;
            this.left = temp;
        }

        this.face += "-B-";

        this.allMoves(mode);
    }

    public void allMoves(int mode) {
        Cube f = new Cube(this);
        f.F(mode);

        Cube fDash = new Cube(this);
        fDash.Fdash(mode);

        Cube f2 = new Cube(this);
        f2.F2(mode);

    }

    






}

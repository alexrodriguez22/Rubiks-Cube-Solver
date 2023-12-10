import java.io.*;
// add jar files for 
//import org.ejml.simple.*;
import java.lang.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class RubiksSolver {

    public static int countF = 0;
    public static int countF2 = 0;
    public static int countFDash = 0;
    public static int mode = 0; // 0 -> BFS, 1 -> DFS
    public static boolean solution = false;

    private Queue<Cube> myqueue;
    private Queue<Cube> myqueue_back;
    private Queue<Cube> myqueue_front;

    private Deque<Cube>[] mystack;
    private Queue<Cube>[] queueForHash;

    private ArrayList<Cube>[] vectorForHash;
    private Cube input;
    private static long check = 0;

    public RubiksSolver() {
        myqueue = new LinkedList<>();
        myqueue_back = new LinkedList<>();
        myqueue_front = new LinkedList<>();


        @SuppressWarnings("unchecked")
        Deque<Cube>[] tempStacks = new Deque[4];
        @SuppressWarnings("unchecked")
        Queue<Cube>[] tempQueuesForHash = new Queue[4];
        @SuppressWarnings("unchecked")
        ArrayList<Cube>[] tempVectorForHash = new ArrayList[49];

        mystack = tempStacks;
        queueForHash = tempQueuesForHash;
        vectorForHash = tempVectorForHash;

        for (int i = 0; i < mystack.length; i++) {
            mystack[i] = new ArrayDeque<>();
            queueForHash[i] = new LinkedList<>();
        }

        for (int i = 0; i < vectorForHash.length; i++) {
            vectorForHash[i] = new ArrayList<>();
        }

        input = new Cube(this);
    }

    // not complete, see line 166 in c++ file for proper threading
    public void pushIn(Cube c, int mode) {
        c.computeCost(); 

        switch (mode) {
            case 1:
                myqueue_front.offer(c);
                break;
            case 2:
                // Replace omp_get_thread_num() with appropriate thread identification logic in Java
                int threadNum = getCurrentThreadNum(); // This is a placeholder
                mystack[threadNum].push(c);
                break;
            case 0:
                myqueue_back.offer(c);
                break;
            case 3:
                // Replace omp_get_thread_num() with appropriate thread identification logic in Java
                threadNum = getCurrentThreadNum(); // This is a placeholder
                queueForHash[threadNum].offer(c);
                break;
            default:
                // Handle invalid mode if necessary
        }
    }

    // NOT COMPLETE You will need to define getCurrentThreadNum method or equivalent logic
    private int getCurrentThreadNum() {
        // Implement thread identification logic
        // Placeholder return
        return 0;
    }


    public void generateQueue(int limit, Cube input, Queue<Cube> myqueue, int mode) {
        if (mode == 3) {
            input.setFace("");
        }

        input.computeCost();
        myqueue.offer(input);

        while (!myqueue.isEmpty()) {
            Cube temp = myqueue.poll();

            if (temp.getCost() == 0 && temp.getLevel() > 0 && mode != 0) {
                if (mode == 3) {
                    return;
                }

                System.out.println("-----------------Solution at level POS3: " + temp.getLevel());
                System.out.println("Path: " + temp.getFace());
                temp.printCube(); // Assuming printCube is a method of Cube
            }

            if (temp.getLevel() == limit) {
                break;
            }

            if (mode == 0) {
                vectorForHash[temp.getCost()].add(temp);
            }

            if (temp.getLevel() < limit) {
                Cube tempL = new Cube(temp);
                Cube tempB = new Cube(temp);
                Cube tempR = new Cube(temp);
                Cube tempU = new Cube(temp);
                Cube tempD = new Cube(temp);

                tempL.setLevel(temp.getLevel() + 1);
                tempB.setLevel(temp.getLevel() + 1);
                tempR.setLevel(temp.getLevel() + 1);
                tempU.setLevel(temp.getLevel() + 1);
                tempD.setLevel(temp.getLevel() + 1);

                if (temp.getLevel() == 0) {
                    temp.allMoves(mode); // Assuming allMoves is a method of Cube
                }

                tempL.orientL(mode);
                myqueue.offer(tempL);

                tempB.orientB(mode);
                myqueue.offer(tempB);

                tempR.orientR(mode);
                myqueue.offer(tempR);

                tempU.orientU(mode);
                myqueue.offer(tempU);

                tempD.orientD(mode);
                myqueue.offer(tempD);
            }
        }
    }
    
    public static boolean arrayMatch(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false; // Returning false on mismatch
            }
        }
        return true; // Returning true if all elements match
    }
    
    public static void printArray(int[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ");
        }
        System.out.println(); // This prints the newline character
    }
    
    public void checkForSolution(String path, Cube c) {
        for (int i = 0; i < path.length(); i++) {
            char move = path.charAt(i);
            if (move == '-') {
                continue;
            } else if (move == 'L') {
                c.orientL(-1);
            } else if (move == 'B') {
                c.orientB(-1);
            } else if (move == 'R') {
                c.orientR(-1);
            } else if (move == 'U') {
                c.orientU(-1);
            } else if (move == 'D') {
                c.orientD(-1);
            } else if (move == 'F') {
                // Check next character to decide which F move to make
                if (i + 1 < path.length()) {
                    char nextMove = path.charAt(i + 1);
                    if (nextMove == '1') {
                        c.F(-1);
                        i++; // Skip the next character as it's part of this move
                    } else if (nextMove == '2') {
                        c.F2(-1);
                        i++; // Skip the next character as it's part of this move
                    } else if (nextMove == '3') {
                        c.Fdash(-1);
                        i++; // Skip the next character as it's part of this move
                    }
                }
            }
        }
        if (c.computeCost() == 0) {
            System.out.println("Solution found through match at: " + c.getFace());
        }
    }

    public void matchCube(Cube c) {
        long total = 0;

        for (Cube temp : vectorForHash[c.getCost()]) {
            if (arrayMatch(temp.getFaceCost(), c.getFaceCost())) {
                Cube t = new Cube(c); // Creating a copy of Cube c
                checkForSolution(temp.getFace(), t);
                total += 1;
            }
        }

        synchronized (RubiksSolver.class) {
            check++; // Incrementing check in a thread-safe manner
        }
    }

    public void printHashMap() {
        long fullCount = 0;
        
        // First loop: Print the size of each ArrayList and total count
        for (int i = 0; i < vectorForHash.length; i++) {
            System.out.println("Vector: " + i + " - " + vectorForHash[i].size());
            fullCount += vectorForHash[i].size();
        }
        System.out.println("Full count: " + fullCount);

        fullCount = 0;
        long t = 0;

        // Second loop: Detailed count of total elements
        for (int i = 0; i < vectorForHash.length; i++) {
            for (int j = 0; j < vectorForHash[i].size(); j++) {
                t++;
                fullCount++;
            }
            System.out.println("Key - " + i + " for: " + t);
            t = 0;
        }
        System.out.println("Full count: " + fullCount);
        System.out.println();
    }

    public void pushInHashMap() {
        while (!myqueue_back.isEmpty()) {
            Cube temp = myqueue_back.poll(); // Retrieves and removes the head of the queue
            vectorForHash[temp.getCost()].add(temp); // Adds the cube to the appropriate ArrayList
        }

        printHashMap(); // Print the state of vectorForHash
    }

    public void optimiseHash() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 49; i++) {
            int index = i;
            executorService.submit(() -> {
                System.out.println("Thread: " + Thread.currentThread().getName());
                for (Cube temp : vectorForHash[index]) {
                    if (temp.getCost() == 0) {
                        continue;
                    }
                    generateQueue(temp.getLevel(), temp, queueForHash[index % queueForHash.length], 3);
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }



    public void depthFirstSearch(int limit, int mode) {
        System.out.println("Depth First Search");

        List<Cube> V = new ArrayList<>(myqueue_front.size());
        while (!myqueue_front.isEmpty()) {
            V.add(myqueue_front.poll());
        }
        System.out.println(V.size());

        long sizeQueue = V.size();
        AtomicBoolean go = new AtomicBoolean(true);

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (int i = 0; i < sizeQueue; i++) {
            int index = i;
            executorService.submit(() -> {
                Cube temp = V.get(index);

                // Check for solution
                if (temp.getCost() == 0) {
                    synchronized (System.out) {
                        System.out.printf("------%s---------Solution at level POS2: %d\n", Thread.currentThread().getName(), temp.getLevel());
                        System.out.println("Path: " + temp.getFace());
                        temp.printCube();
                    }
                    go.set(false);
                }

                mystack[Thread.currentThread().hashCode() % mystack.length].push(temp);

                while (!mystack[Thread.currentThread().hashCode() % mystack.length].isEmpty() && go.get()) {
                    Cube t = mystack[Thread.currentThread().hashCode() % mystack.length].pop();

                    // Check for solution
                    if (t.getCost() == 0) {
                        synchronized (System.out) {
                            System.out.printf("--------%s---------Solution at level POS1: %d\n", Thread.currentThread().getName(), t.getLevel());
                            System.out.println("Path: " + t.getFace());
                            t.printCube();
                        }
                        go.set(false);
                    }

                    if (t.getLevel() < limit && go.get()) {
                        t.setLevel(t.getLevel() + 1);

                        // Apply orientations and push to stack
                        applyOrientationsAndPush(t, mode);
                    }
                }
            });
        }

        executorService.shutdown();
    }

    private void applyOrientationsAndPush(Cube t, int mode) {
        // Get the stack corresponding to the current thread
        Deque<Cube> stack = mystack[Thread.currentThread().hashCode() % mystack.length];
    
        // Apply 'orientL' move and push
        Cube tempL = new Cube(t);
        tempL.orientL(mode);
        stack.push(tempL);
    
        // Apply 'orientB' move and push
        Cube tempB = new Cube(t);
        tempB.orientB(mode);
        stack.push(tempB);
    
        // Apply 'orientR' move and push
        Cube tempR = new Cube(t);
        tempR.orientR(mode);
        stack.push(tempR);
    
        // Apply 'orientU' move and push
        Cube tempU = new Cube(t);
        tempU.orientU(mode);
        stack.push(tempU);
    
        // Apply 'orientD' move and push
        Cube tempD = new Cube(t);
        tempD.orientD(mode);
        stack.push(tempD);
    
    }

    public static void main(String[] args) {
        int limitBFS = 5;
        int hashlen = 3;
        int limitDFS = 6;


        RubiksSolver solver = new RubiksSolver();
         // Create cubes by passing the solver to the constructor
         Cube solved = new Cube(solver);
         Cube input = new Cube(solver);

        // Setting up the solved cube
        solved.setLevel(0);
        solved.setUp(new int[][]{{6, 6, 6}, {6, 6, 6}, {6, 6, 6}});
        solved.setFront(new int[][]{{5, 5, 5}, {5, 5, 5}, {5, 5, 5}});
        solved.setLeft(new int[][]{{4, 4, 4}, {4, 4, 4}, {4, 4, 4}});
        solved.setBack(new int[][]{{3, 3, 3}, {3, 3, 3}, {3, 3, 3}});
        solved.setRight(new int[][]{{2, 2, 2}, {2, 2, 2}, {2, 2, 2}});
        solved.setDown(new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}});

        // Setting up the input cube
        input.setLevel(0);
        input.setUp(new int[][]{{1, 6, 6}, {4, 6, 4}, {1, 6, 5}});
        input.setFront(new int[][]{{5, 3, 6}, {1, 5, 2}, {3, 1, 2}});
        input.setLeft(new int[][]{{2, 5, 2}, {2, 4, 5}, {2, 1, 1}});
        input.setBack(new int[][]{{4, 4, 3}, {6, 3, 3}, {5, 6, 3}});
        input.setRight(new int[][]{{4, 3, 3}, {5, 2, 2}, {5, 3, 4}});
        input.setDown(new int[][]{{4, 2, 6}, {4, 1, 1}, {6, 5, 1}});


        // Setting up the executor service for parallel tasks
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submitting two tasks to the executorService, one for each thread
        executorService.submit(() -> {
            System.out.println("Thread 1 running");
            solver.generateQueue(limitBFS, input, myqueue_front, 1);
        });

        executorService.submit(() -> {
            System.out.println("Thread 2 running");
            solver.generateQueue(hashlen, solved, myqueue_back, 0);
            System.out.println("Size of queue: " + solver.getQueueBackSize());
            solver.pushInHashMap();
        });

        // Shut down the executor and wait for tasks to finish
        executorService.shutdown();
        // Add code to await termination of the executor service here

        System.out.println("---------------------------------------------------------");

        // After the parallel tasks are finished, proceed with the optimization
        solver.optimiseHash();

        // If a solution has not been found, proceed with depth-first search
        if (!solver.isSolutionFound()) {
            solver.depthFirstSearch(limitDFS, 2);
        }

        System.out.println("-----");
        System.out.println("Size of queue: " + solver.getQueueBackSize());
        System.out.println("Size of queue: " + solver.getQueueFrontSize());
        System.out.println("Check: " + RubiksSolver.getCheck());
    
        
    }

}
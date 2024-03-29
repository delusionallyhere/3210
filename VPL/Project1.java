    /*
    Original code written by Dr. Jerry Shultz
    Additional code written by Raymond Ortiz
    */
import java.io.*;
import java.util.*;
//import java.util.logging.Logger;
//import java.util.stream.Collectors;

public class Project1
{
  static Scanner keys;

  static int max;
  static int[] mem;
  static int ip, bp, sp, rv, hp, numPassed, gp;
  static int step;
  static int stackCount = 0;                                                    // Used to determine which cell in the heap to access when instructions 24/25 are called.

  static String fileName;

  static boolean replacedLabels = false;                           // Used to keep track of which cell to push onto when instruction 3 is called.

  static int heapIndex = hp;

  static boolean debug = false;

  public static void main(String[] args) throws Exception {

    keys = new Scanner( System.in );

    if( args.length != 2 ) {
        System.out.println("Usage: jcr Project1 <vpl program> <memory size>" );
        System.exit(1);
    }

    fileName = args[0];

    max = Integer.parseInt( args[1] );
    mem = new int[max];

    // load the program into the front part of
    // memory
    Scanner input = new Scanner( new File( fileName ) );
    String line;
    StringTokenizer st;
    int opcode;

    ArrayList<IntPair> labels, holes;
    labels = new ArrayList<IntPair>();
    holes = new ArrayList<IntPair>();
    int label;

    // load the code

    int k=0;
    while ( input.hasNextLine() ) {
      line = input.nextLine();
      System.out.println("parsing line [" + line + "]");
      if( line != null )
      {// extract any tokens
        st = new StringTokenizer( line );
        if( st.countTokens() > 0 )
        {// have a token, so must be an instruction (as opposed to empty line)

          opcode = Integer.parseInt(st.nextToken());

          // load the instruction into memory:

          if( opcode == labelCode )
          {// note index that comes where label would go
            label = Integer.parseInt(st.nextToken());
            labels.add( new IntPair( label, k ) );
          }
          else if( opcode == noopCode ){
          }
          else
          {// opcode actually gets stored
            mem[k] = opcode;  k++;

            if( opcode == callCode || opcode == jumpCode ||
                opcode == condJumpCode )
            {// note the hole immediately after the opcode to be filled in later
              label = Integer.parseInt( st.nextToken() );
              mem[k] = label;  holes.add( new IntPair( k, label ) );
              ++k;
            }

            // load correct number of arguments (following label, if any):
            for( int j=0; j<numArgs(opcode); ++j )
            {
              mem[k] = Integer.parseInt(st.nextToken());
              ++k;
            }

          }// not a label

        }// have a token, so must be an instruction
      }// have a line
    }// loop to load code

    //System.out.println("after first scan:");
    //showMem( 0, k-1 );

    // fill in all the holes:
    int index;
    for( int m=0; m<holes.size(); ++m )
    {
      label = holes.get(m).second; // iterate on every second element
      index = -1;
      for( int n=0; n<labels.size(); ++n )
        if( labels.get(n).first == label )
          index = labels.get(n).second;
      mem[ holes.get(m).first ] = index;
    }

    System.out.println("after replacing labels:");
    showMem( 0, k-1 );
    replacedLabels = true;

    // initialize registers:
    bp = k;  sp = k+2;  ip = 0;  rv = -1;  hp = max;
    numPassed = 0;

    int bpOffset = bp + 2;                                         // To store the real bp (accounting for return ip and return bp)

    int codeEnd = bp-1;

    System.out.println("Code is " );
    showMem( 0, codeEnd );

    gp = codeEnd + 1;

    // start execution:
    boolean done = false;
    int op, a=0, b=0, c=0;
    int actualNumArgs;

    int step = 0;

    int oldIp = 0;

    do {
        if (debug == true) {
            // show details of current step
            System.out.println("--------------------------");
            System.out.println("Step of execution with IP = " + ip + " opcode: " +
              mem[ip] +
             " bp = " + bp + " sp = " + sp + " hp = " + hp + " rv = " + rv + " gp = " + gp );
            System.out.println(" chunk of code: " +  mem[ip] + " " +
                                mem[ip+1] + " " + mem[ip+2] + " " + mem[ip+3] );
            System.out.println("--------------------------");
            System.out.println( " memory from " + (codeEnd+1) + " up: " );
            showMem( codeEnd+1, sp+3 );
            System.out.println("hit <enter> to go on" );
            keys.nextLine();
        }

      oldIp = ip;

      op = mem[ip];
      ip++;

      a = -1;  b = -2;  c = -3;                                    // extract the args into a, b, c


      if(op == callCode || op == jumpCode ||
                op == condJumpCode) {
        actualNumArgs = numArgs(op) + 1;
      }

      else
        actualNumArgs = numArgs(op);

      if(actualNumArgs == 1) {
          a = mem[ip];
          ip++;
      }

      else if(actualNumArgs == 2) {
          a = mem[ip];
          ip++;
          b = mem[ip];
           ip++;
       }

      else if(actualNumArgs == 3) {
          a = mem[ip];
          ip++;
          b = mem[ip];
          ip++;
          c = mem[ip];
          ip++;
      }

      switch(op) {

        case noopCode:                                       // 0
            //  Do nothing
            break;

        case labelCode:                                       // 1
            /*
            During program loading this instruction disappears, and all occurences of L
            are replaced by the actual index in mem where the opcode 1 would have
            been stored.
            */
            System.out.println("1, This code should never be reached - error.");
            System.exit(1);
            break;

        case callCode:                                          // 2
            /*
            Do all thee steps necessary to set up for execution of the subprogram that
            begins at L.
            */
            int returnBp = bp;
            int returnIp = ip;

            ip = a;
            bp = sp;                                                  // Move onto a new stack frame
            bpOffset = bp + 2;
            sp += stackCount+2;
            stackCount = 0;
            mem[bp] = returnIp;                         //set return bp
            mem[bp + 1] = returnBp;                  //set return ip
            break;

        case passCode:                                       // 3
            //  Push the contents of cell a on the stack.
            mem[sp + 2 + stackCount] = mem[bpOffset + a];
            stackCount++;
            break;

        case allocCode:                                        // 4
            //  Increase sp by n to make space for local variables in the current stack frame.
            sp += a;
            break;

        case returnCode:                                      // 5
            /*
            Do all the steps necessary to return from the current subprogram, including
            putting the value stored in cell a in rv.
            */
            rv = mem[bpOffset + a];
            sp = bp;
            stackCount = 0;
            ip = mem[bp];
            bp = mem[bp + 1];
            bpOffset = bp + 2;
            break;

        case getRetvalCode:                                // 6
            //  Copy the value stored in rv into cell a.
            mem[bpOffset + a] = rv;
            break;

          case jumpCode:                                       // 7
              /*
             Change ip to L.
             The argument a has already been changed to be the instruction number
              we're supposed to jump to.
             */
              ip = a;
              break;

          case condJumpCode:                            // 8
              /*
              If the value stored in cell a is non-zero, change ip to L, otherwise
              move ip to the next instruction.
             */
              if(mem[bpOffset + b] != 0) {
                  ip = a;
              }
              break;

        case addCode:                                             // 9
            //  Add the values in cell b and cell c and store the result in cell a.
            mem[bpOffset + a] = mem[bpOffset + b] + mem[bpOffset + c];
            break;

        case subCode:                                             // 10
            //  Same as 9, but do cell b - cell c.
            mem[bpOffset + a] = mem[bpOffset + b] - mem[bpOffset + c];
            break;

        case multCode:                                           // 11
            //  Same as 9, but do cell b * cell c.
            mem[bpOffset + a] = mem[bpOffset + b] * mem[bpOffset + c];
            break;

        case divCode:                                               // 12
            //  Same as 9, but do cell b / cell c.
            mem[bpOffset + a] = mem[bpOffset + b] / mem[bpOffset + c];
            break;

        case remCode:                                             // 13
            //  Same as 9, but do cell b % cell c.
            mem[bpOffset + a] = mem[bpOffset + b] % mem[bpOffset + c];
            break;

        case equalCode:                                          // 14
            //  Same as 9, but do cell b == cell c.
            if (mem[bpOffset + b] == mem[bpOffset + c]) {
                mem[bpOffset + a] = 1;
            }
            else
                mem[bpOffset + a] = 0;
            break;

        case notEqualCode:                                  // 15
            //  Same as 9, but do cell b != cell c.
            if (mem[bpOffset + b] != mem[bpOffset + c]) {
                mem[bpOffset + a] = 1;
            }
            else
                mem[bpOffset + a] = 0;
            break;

        case lessCode:                                             // 16
            //  Same as 9, but do cell b < cell c.
            if (mem[bpOffset + b] < mem[bpOffset + c]) {
                mem[bpOffset + a] = 1;
            }
            else
                mem[bpOffset + a] = 0;
            break;

        case lessEqualCode:                                  // 17
            //  Same as 9, but do cell b <= cell c.
            if (mem[bpOffset + a] <= mem[bpOffset + c]) {
                mem[bpOffset + a] = 1;
            }
            else
                mem[bpOffset + a] = 0;
            break;

        case andCode:                                             // 18
            //  Same as 9, but do cell b && cell c.
            if (mem[bpOffset + b] != 0 && mem[bpOffset + c] != 0) {
                mem[bpOffset + a] = 1;
            }
            else
                mem[bpOffset + a] = 0;
            break;

        case orCode:                                                 // 19
            //  Same as 9, but do cell b || cell c.
            if (mem[bpOffset + b] != 0 || mem[bpOffset + c] != 0) {
                mem[bpOffset + a] = 1;
            }
            else
                mem[bpOffset + a] = 0;
            break;

        case notCode:                                               // 20
            //  If cell b == 0, put 1 in cell a, otherwise, put 0 in cell a.
            if (mem[bpOffset + b] == 0) {
                mem[bpOffset + a] = 1;
            }
            else
                mem[bpOffset + a] = 0;
            break;

        case oppCode:                                               // 21
            //  Put the opposite of the contents of cell b in cell a.
            mem[bpOffset + a] = 0 - mem[bpOffset + b];
            break;

        case litCode:                                                   // 22
            //  Put n in cell a.
            mem[bpOffset + a] = b;
            break;

        case copyCode:                                             // 23
            //  Copy the value in cell b into cell a.
            mem[bpOffset + a] = mem[bpOffset + b];
            break;

        case getCode:                                                 // 24
            /*
            Get the value stored in the heap at the index obtained by adding the value of
            cell b and the value of cell c and copy it into cell a.
            */
            heapIndex = mem[bpOffset + b] + mem[bpOffset + c];
            mem[bpOffset + a] = mem[heapIndex];
            break;

        case putCode:                                                 // 25
            /*
            Take the value from cell c and store it in the heap at the location with index
            computed as the value in cell a plus the value in cell b.
            */
            heapIndex = mem[bpOffset + a] + mem[bpOffset + b];
            mem[heapIndex] = mem[bpOffset + c];
            break;

        case haltCode:                                                // 26
            //  Halt execution.
            System.exit(0);

        case inputCode:                                             // 27
            /*
            Print a ? and a space in the console and wait for an integer value to be typed
            by the user, and then store it in cell a.
            */
            //System.out.format("DEBUG: bpOffset: %d, a: %d, mem[bpOffset + a]: %d%n", bpOffset, a, mem[bpOffset + a]);
            //Scanner userIn = new Scanner(System.in);
            System.out.print("? ");
            try {
                mem[bpOffset +a] = keys.nextInt();
                //System.out.format("DEBUG: mem[bpOffset + a] = %d%n", mem[bpOffset + a]);
            }
            catch (java.util.InputMismatchException e1) {
               System.out.print("You must input an integer");
               System.exit(1);
            }
            break;

        case outputCode:                                            // 28
            //  Display the value stored in call a in the console
            System.out.print(mem[bpOffset + a]);
            break;

        case newlineCode:                                           // 29
            //  Move the console cursor to the beginning of the next line
            System.out.print("\n");
            break;

        case symbolCode:                                            // 30
            /*
            If the value stored in cell a is between 32 and 126, display the corresponding symbol
            at the console cursor, otherwise do nothing.
            */
            int val = mem[bpOffset + a];
            if (val >= 32 && val <= 126) {
                System.out.print((char) val);
            }
            else
                System.out.format("30, entry: %d is not a valid ascii character.", val);
            break;

        case newCode:                                                  // 31
            /*
            Let the value stored in cell b be denoted by m. Decrease hp by m and put the new value
            of hp in cell a
            */
            int m = mem[bpOffset + b];
            hp -= m;
            mem[bpOffset + a] = hp;
            break;

        case allocGlobalCode:                                    // 32
            /*
            This instruction must occur first in any program that uses it. It simply sets the initial
            value of sp to n cells beyond the end of stored program memory, and sets gp to the end of
            stored program memory.
            */
            if (ip == 2) {
                gp = codeEnd+1;
                bp += a;
                bpOffset = bp+2;
                sp += a;
            }
            else {
                System.out.println("Error, 32 must be the FIRST instruction, there was one at " + ip);
                System.exit(1);
            }
            break;

        case toGlobalCode:                                          // 33
            //  Copy the contents of cell a to the global memory area at index gp+n.
            mem[gp + a] = mem[bpOffset + b];
            break;

        case fromGlobalCode:                                     // 34
            //  Copy the contents of the global memory cell at index gp+n into cell a.
            mem[bpOffset + a] = mem[gp + b];
            break;

        default:
            System.out.format("Fatal error at ip: %d, unknown opcode [%d]", ip, op);
            System.exit(1);
      }

      step++;

    }while( !done );


  }// main

  // use symbolic names for all opcodes:

  // op to produce comment
  private static final int noopCode = 0;

  // ops involved with registers
  private static final int labelCode = 1;
  private static final int callCode = 2;
  private static final int passCode = 3;
  private static final int allocCode = 4;
  private static final int returnCode = 5;  // return a means "return and put
           // copy of value stored in cell a in register rv
  private static final int getRetvalCode = 6;//op a means "copy rv into cell a"
  private static final int jumpCode = 7;
  private static final int condJumpCode = 8;

  // arithmetic ops
  private static final int addCode = 9;
  private static final int subCode = 10;
  private static final int multCode = 11;
  private static final int divCode = 12;
  private static final int remCode = 13;
  private static final int equalCode = 14;
  private static final int notEqualCode = 15;
  private static final int lessCode = 16;
  private static final int lessEqualCode = 17;
  private static final int andCode = 18;
  private static final int orCode = 19;
  private static final int notCode = 20;
  private static final int oppCode = 21;

  // ops involving transfer of data
  private static final int litCode = 22;  // litCode a b means "cell a gets b"
  private static final int copyCode = 23;// copy a b means "cell a gets cell b"
  private static final int getCode = 24; // op a b means "cell a gets
                                                // contents of cell whose
                                                // index is stored in b"
  private static final int putCode = 25;  // op a b means "put contents
     // of cell b in cell whose offset is stored in cell a"

  // system-level ops:
  private static final int haltCode = 26;
  private static final int inputCode = 27;
  private static final int outputCode = 28;
  private static final int newlineCode = 29;
  private static final int symbolCode = 30;
  private static final int newCode = 31;

  // global variable ops:
  private static final int allocGlobalCode = 32;
  private static final int toGlobalCode = 33;
  private static final int fromGlobalCode = 34;

  // debug ops:
  private static final int debugCode = 35;

  // return the number of arguments after the opcode,
  // except ops that have a label return number of arguments
  // after the label, which always comes immediately after
  // the opcode
  private static int numArgs( int opcode )
  {
    // highlight specially behaving operations
    if( opcode == labelCode ) return 1;  // not used
    else if( opcode == jumpCode ) return 0;  // jump label
    else if( opcode == condJumpCode ) return 1;  // condJump label expr
    else if( opcode == callCode ) return 0;  // call label

    // for all other ops, lump by count:

    else if( opcode==noopCode ||
             opcode==haltCode ||
             opcode==newlineCode ||
             opcode==debugCode
           )
      return 0;  // op

    else if( opcode==passCode || opcode==allocCode ||
             opcode==returnCode || opcode==getRetvalCode ||
             opcode==inputCode ||
             opcode==outputCode || opcode==symbolCode ||
             opcode==allocGlobalCode
           )
      return 1;  // op arg1

    else if( opcode==notCode || opcode==oppCode ||
             opcode==litCode || opcode==copyCode || opcode==newCode ||
             opcode==toGlobalCode || opcode==fromGlobalCode

           )
      return 2;  // op arg1 arg2

    else if( opcode==addCode ||  opcode==subCode || opcode==multCode ||
             opcode==divCode ||  opcode==remCode || opcode==equalCode ||
             opcode==notEqualCode ||  opcode==lessCode ||
             opcode==lessEqualCode || opcode==andCode ||
             opcode==orCode || opcode==getCode || opcode==putCode
           )
      return 3;

    else
    {
      System.out.format("Fatal error at ip: %d, unknown opcode [%d]%n", ip, opcode);
      System.exit(1);
      return -1;
    }

  }// numArgs

  private static void showMem( int a, int b )
  {
    for( int k=a; k<=b; ++k )
    {
      if(k-bp < 0 || replacedLabels == false) {
         System.out.format("%d: %d%n", k, mem[k]);
      } else if(k-bp == 0) {
        System.out.println("-------STACK FRAME BEGINS-------");
        System.out.format("ip: %d - stack cell: X - value: %d%n", k, mem[k]);
      } else if (k == sp) {
        System.out.println("-------STACK FRAME ENDS-------");
        System.out.format("ip: %d - stack cell: %d - value: %d%n", k, (k-bp-2), mem[k]);
      } else if(k-bp-2 >= 0){
        System.out.format("ip: %d - stack cell: %d - value: %d%n", k, (k-bp-2), mem[k]);
      } else {
        System.out.format("ip: %d - stack cell: X - value: %d%n", k, mem[k]);
      }
      //System.out.println( k + ": " + mem[k] );
    }
  }// showMem

}// Project1

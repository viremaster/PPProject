import Sprockell

prog :: [Instruction]
prog = [ 
       Load (ImmValue 3) regA, 
       Push regA, 
       Pop regA, 
       Load (ImmValue (-1)) regB, 
       Compute Mul regA regB regA, 
       Push regA, 
       Pop regA, 
       Store regA (DirAddr 0), 
       Load (DirAddr 0) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       Load (ImmValue 0) regA, 
       Push regA, 
       Pop regA, 
       Store regA (DirAddr 4), 
       Load (DirAddr 4) regA, 
       Push regA, 
       Pop regA, 
       Load (ImmValue 1) regB, 
       Compute Xor regA regB regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       EndProg
       ]

main = run [prog]
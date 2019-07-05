import Sprockell

prog :: [Instruction]
prog = [ 
       Load (ImmValue 1) regA, 
       Push regA, 
       Pop regA, 
       Store regA (DirAddr 0), 
       Load (ImmValue 0) regA, 
       Push regA, 
       Pop regA, 
       Store regA (DirAddr 4), 
       Load (DirAddr 0) regA, 
       Push regA, 
       Load (DirAddr 4) regA, 
       Push regA, 
       Pop regA, 
       Pop regB, 
       Compute Or regA regB regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       Load (DirAddr 0) regA, 
       Push regA, 
       Load (DirAddr 4) regA, 
       Push regA, 
       Pop regA, 
       Pop regB, 
       Compute And regA regB regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       EndProg
       ]

main = run [prog]
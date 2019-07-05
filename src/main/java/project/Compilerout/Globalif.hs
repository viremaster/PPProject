import Sprockell

prog :: [Instruction]
prog = [ 
       Load (ImmValue 4) regA, 
       Push regA, 
       Load (ImmValue 0) regA, 
       Push regA, 
       Pop regB, 
       Pop regA, 
       Compute Equal regA regB regA, 
       Push regA, 
       Pop regA, 
       Branch regA (Rel 6), 
       Load (ImmValue 5) regA, 
       Push regA, 
       Pop regA, 
       Store regA (DirAddr 0), 
       Jump (Rel 5), 
       Load (ImmValue 0) regA, 
       Push regA, 
       Pop regA, 
       Store regA (DirAddr 4), 
       EndProg
       ]

main = run [prog]
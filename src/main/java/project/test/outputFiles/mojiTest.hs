import Sprockell

prog :: [Instruction]
prog = [ 
       Load (ImmValue 6) regA, 
       Push regA, 
       Pop regA, 
       Store regA null, 
       Load null regA, 
       Push regA, 
       Load (ImmValue 1) regA, 
       Push regA, 
       Pop regB, 
       Pop regA, 
       Compute Add regA regB regA, 
       Push regA, 
       Pop regA, 
       Store regA null, 
       Load null regA, 
       Push regA, 
       Pop regA, 
       Store regA null, 
       EndProg
       ]

main = run [prog]
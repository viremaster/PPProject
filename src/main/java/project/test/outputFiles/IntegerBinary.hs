import Sprockell

prog :: [Instruction]
prog = [ 
       Load (ImmValue 3) regA, 
       Push regA, 
       Pop regA, 
       Store regA (DirAddr 0), 
       Load (ImmValue 3) regA, 
       Push regA, 
       Pop regA, 
       Store regA (DirAddr 4), 
       Load (DirAddr 0) regA, 
       Push regA, 
       Load (DirAddr 4) regA, 
       Push regA, 
       Pop regB, 
       Pop regA, 
       Compute Add regA regB regA, 
       Push regA, 
       Pop regA, 
       Store regA (DirAddr 8), 
       Load (DirAddr 8) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       Load (DirAddr 0) regA, 
       Push regA, 
       Load (DirAddr 4) regA, 
       Push regA, 
       Pop regB, 
       Pop regA, 
       Compute Sub regA regB regA, 
       Push regA, 
       Pop regA, 
       Store regA (DirAddr 8), 
       Load (DirAddr 8) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       Load (DirAddr 0) regA, 
       Push regA, 
       Load (DirAddr 4) regA, 
       Push regA, 
       Pop regB, 
       Pop regA, 
       Compute Mul regA regB regA, 
       Push regA, 
       Pop regA, 
       Store regA (DirAddr 8), 
       Load (DirAddr 8) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       Load (DirAddr 0) regA, 
       Push regA, 
       Load (DirAddr 4) regA, 
       Push regA, 
       Pop regB, 
       Pop regA, 
       Load (ImmValue 0) regC, 
       Compute Lt regA reg0 regE, 
       Compute Lt regB reg0 regF, 
       Branch regE (Rel 3), 
       Branch regF (Rel 14), 
       Jump (Rel 27),
       Branch regF (Rel 23), 
       Load (ImmValue (-1)) regE, 
       Compute Mul regA regE regA, 
       Compute GtE regA regB regA, 
       Branch regA (Rel 2), 
       Jump (Rel 5), 
       Load (ImmValue 1) regD, 
       Compute Add regC regD regC, 
       Compute Sub regA regB regA, 
       Jump (Rel (-6)), 
       Compute Mul regC regE regC, 
       Jump (Rel 22), 
       Load (ImmValue (-1)) regE, 
       Compute Mul regB regE regB, 
       Compute GtE regA regB regA, 
       Branch regA (Rel 2), 
       Jump (Rel 5), 
       Load (ImmValue 1) regD, 
       Compute Add regC regD regC, 
       Compute Sub regA regB regA, 
       Jump (Rel (-6)), 
       Compute Mul regC regE regC, 
       Jump (Rel 11), 
       Load (ImmValue (-1)) regE, 
       Compute Mul regB regE regB, 
       Compute Mul regA regE regA, 
       Compute GtE regA regB regA, 
       Branch regA (Rel 2), 
       Jump (Rel 5), 
       Load (ImmValue 1) regD, 
       Compute Add regC regD regC, 
       Compute Sub regA regB regA, 
       Jump (Rel (-6)), 
       Push regC, 
       Pop regA, 
       Store regA (DirAddr 8), 
       Load (DirAddr 8) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       EndProg
       ]

main = run [prog]
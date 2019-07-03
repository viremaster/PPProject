import Sprockell

prog :: [Instruction]
prog = [ 
       Compute Equal regSprID reg0 regB, 
       Branch regB (Rel 21), 
       TestAndSet (DirAddr 1), 
       Receive regA, 
       Compute Equal regA reg0 regB, 
       Branch regB (Rel (-3)), 
       ReadInstr (DirAddr 3), 
       Receive regA, 
       Load (ImmValue 1) regB, 
       Compute Add regA regB regA, 
       WriteInstr regA (DirAddr 3), 
       ReadInstr (DirAddr 3), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       ReadInstr (DirAddr 2), 
       Receive regA, 
       Compute Equal regA reg0 regB, 
       Branch regB (Rel (-3)), 
       WriteInstr reg0 (DirAddr 2), 
       WriteInstr reg0 (DirAddr 1), 
       Jump (Ind regA), 
       Load (ImmValue 0) regA, 
       Push regA, 
       Pop regA, 
       Store regA (DirAddr 0), 
       ReadInstr (DirAddr 4), 
       Receive regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       ReadInstr (DirAddr 4), 
       Receive regA, 
       Push regA, 
       Load (ImmValue 7) regA, 
       Push regA, 
       Pop regA, 
       Pop regB, 
       Compute Lt regA regB regA, 
       Push regA, 
       Pop regA, 
       Branch regA (Abs 44), 
       Jump (Abs 65), 
       ReadInstr (DirAddr 4), 
       Receive regA, 
       Push regA, 
       Load (ImmValue 1) regA, 
       Push regA, 
       Pop regA, 
       Pop regB, 
       Compute Add regA regB regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA (DirAddr 4), 
       ReadInstr (DirAddr 4), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       ReadInstr (DirAddr 4), 
       Receive regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       Jump (Abs 32), 
       ReadInstr (DirAddr 4), 
       Receive regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       ReadInstr (DirAddr 4), 
       Receive regA, 
       Push regA, 
       Load (ImmValue 0) regA, 
       Push regA, 
       Pop regA, 
       Pop regB, 
       Compute Gt regA regB regA, 
       Push regA, 
       Pop regA, 
       Branch regA (Abs 82), 
       Jump (Abs 98), 
       ReadInstr (DirAddr 4), 
       Receive regA, 
       Push regA, 
       Load (ImmValue 1) regA, 
       Push regA, 
       Pop regA, 
       Pop regB, 
       Compute Sub regA regB regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA (DirAddr 4), 
       ReadInstr (DirAddr 4), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       Jump (Abs 70), 
       ReadInstr (DirAddr 4), 
       Receive regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       EndProg
       ]

main = run [prog]
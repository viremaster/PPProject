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
       WriteInstr regA (DirAddr 4), 
       ReadInstr (DirAddr 4), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       Load (ImmValue 0) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA (DirAddr 5), 
       ReadInstr (DirAddr 5), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       Load (ImmValue 0) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA (DirAddr 6), 
       ReadInstr (DirAddr 6), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       TestAndSet (DirAddr 0), 
       Receive regA, 
       Compute Equal regA reg0 regB, 
       Branch regB (Rel (-3)), 
       Load (ImmValue 57) regA, 
       WriteInstr regA (DirAddr 2), 
       ReadInstr (DirAddr 2), 
       Receive regA, 
       Branch regA (Rel (-2)), 
       WriteInstr reg0 (DirAddr 0), 
       Jump (Rel  71), 
       Load (ImmValue 1) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA (DirAddr 4), 
       ReadInstr (DirAddr 4), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       Load (ImmValue 1) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA (DirAddr 6), 
       ReadInstr (DirAddr 6), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       ReadInstr (DirAddr 5), 
       Receive regA, 
       Push regA, 
       Load (ImmValue 1) regA, 
       Push regA, 
       Pop regB, 
       Pop regA, 
       Compute Equal regA regB regA, 
       Push regA, 
       ReadInstr (DirAddr 6), 
       Receive regA, 
       Push regA, 
       Load (ImmValue 1) regA, 
       Push regA, 
       Pop regB, 
       Pop regA, 
       Compute Equal regA regB regA, 
       Push regA, 
       Pop regA, 
       Pop regB, 
       Compute And regA regB regA, 
       Push regA, 
       Pop regA, 
       Branch regA (Rel 2), 
       Jump (Rel 2), 
       Jump (Rel (-25)), 
       ReadInstr (DirAddr 6), 
       Receive regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       Load (ImmValue 0) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA (DirAddr 4), 
       ReadInstr (DirAddr 4), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       TestAndSet (DirAddr 1), 
       Receive regA, 
       Compute Equal regA reg0 regB, 
       Branch regB (Rel (-3)), 
       ReadInstr (DirAddr 3), 
       Receive regB, 
       Load (ImmValue 1) regB, 
       Compute Sub regA regB regA, 
       WriteInstr regA (DirAddr 3), 
       ReadInstr (DirAddr 3), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       WriteInstr reg0 (DirAddr 1), 
       EndProg, 
       TestAndSet (DirAddr 0), 
       Receive regA, 
       Compute Equal regA reg0 regB, 
       Branch regB (Rel (-3)), 
       Load (ImmValue 138) regA, 
       WriteInstr regA (DirAddr 2), 
       ReadInstr (DirAddr 2), 
       Receive regA, 
       Branch regA (Rel (-2)), 
       WriteInstr reg0 (DirAddr 0), 
       Jump (Rel  71), 
       Load (ImmValue 1) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA (DirAddr 5), 
       ReadInstr (DirAddr 5), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       Load (ImmValue 0) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA (DirAddr 6), 
       ReadInstr (DirAddr 6), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       ReadInstr (DirAddr 4), 
       Receive regA, 
       Push regA, 
       Load (ImmValue 1) regA, 
       Push regA, 
       Pop regB, 
       Pop regA, 
       Compute Equal regA regB regA, 
       Push regA, 
       ReadInstr (DirAddr 6), 
       Receive regA, 
       Push regA, 
       Load (ImmValue 0) regA, 
       Push regA, 
       Pop regB, 
       Pop regA, 
       Compute Equal regA regB regA, 
       Push regA, 
       Pop regA, 
       Pop regB, 
       Compute And regA regB regA, 
       Push regA, 
       Pop regA, 
       Branch regA (Rel 2), 
       Jump (Rel 2), 
       Jump (Rel (-25)), 
       ReadInstr (DirAddr 6), 
       Receive regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA numberIO, 
       Load (ImmValue 0) regA, 
       Push regA, 
       Pop regA, 
       WriteInstr regA (DirAddr 5), 
       ReadInstr (DirAddr 5), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       TestAndSet (DirAddr 1), 
       Receive regA, 
       Compute Equal regA reg0 regB, 
       Branch regB (Rel (-3)), 
       ReadInstr (DirAddr 3), 
       Receive regB, 
       Load (ImmValue 1) regB, 
       Compute Sub regA regB regA, 
       WriteInstr regA (DirAddr 3), 
       ReadInstr (DirAddr 3), 
       Receive regB, 
       Compute NEq regA regB regC, 
       Branch regC (Rel (-4)), 
       WriteInstr reg0 (DirAddr 1), 
       EndProg, 
       EndProg
       ]

main = run [prog, prog, prog]
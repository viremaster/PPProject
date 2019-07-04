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
       EndProg
       ]

main = run [prog, prog]
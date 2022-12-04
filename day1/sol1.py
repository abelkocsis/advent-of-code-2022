file1 = open('in1.txt', 'r')
Lines = file1.readlines()
  
max_number = 0
current_elf = 0
elf_snacks = [] 

for line in Lines:
    if line == "\n":
        elf_snacks.append(current_elf) 
        current_elf = 0
    else:
       current_elf += int(line) 

sorted_snacks = sorted(elf_snacks)
sorted_snacks.reverse()

print("Calories for Elf carrying the most: ", sorted_snacks[0])
print("Calories for top three Elf carrying the most:  ", sum(sorted_snacks[:3]))
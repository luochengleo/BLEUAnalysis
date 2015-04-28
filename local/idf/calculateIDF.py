from collections import defaultdict

count = defaultdict(lambda:0)

for l in open('allResultTextSegs.txt').readlines():
    count[l.strip().replace('\t','')] +=1

fout = open('df.txt','w')
for k in count :
    fout.write(k+'\t'+str(count[k])+'\n')
fout.close()
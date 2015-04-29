#coding=utf8

from bs4 import BeautifulSoup
import sqlite3
import sys
from collections import defaultdict
reload(sys)
sys.setdefaultencoding('utf-8')

cx = sqlite3.connect('db.sqlite3')
cu = cx.cursor()

durationcount = defaultdict(lambda:0)
fout = open('userFixedContent.txt','w')

count = 0
for l in open('fixitionOnResult.feature.temp').readlines()[1:]:
    segs = l.strip().split('\t')
    sid = int(segs[0])
    task = int(segs[1])
    pageid = int(segs[3])
    resultid = int(segs[10])
    duration = int(segs[8])
    rid = 'rb_'+str(10*(pageid-1)+resultid-1)
    query = segs[2]

    durationcount[(sid,task,query,rid)]+= duration

for k in durationcount:
    sid,task,query,rid = k
    duration = durationcount[k]
    count +=1
    if count % 100==1:
        print count,len(durationcount.keys())
    cu.execute('select * from anno_searchresult where query="'+query+'" and result_id="'+rid+'"')

    one = cu.fetchone()
    if one==None:
        break
    else:
        content=one[4]
        soup = BeautifulSoup(content)
        title = soup.find('h3').get_text()
        snippet = soup.find('div',class_='ft').get_text()
        fout.write(str(sid)+'\t'+str(task)+'\t'+str(duration)+'\t'+(title +' '+snippet).replace('\n',' ')+'\n')
fout.close()

import os
import json
import glob
from nltk import regexp_tokenize, PorterStemmer


def add_word(words, word, file_name):
    if word not in words:
        words[word] = {'doc_count' : 1, 'docs' : {file_name : 1}}
    elif file_name not in words[word]['docs']:
        words[word]['docs'][file_name] = 1;
    else:
        words[word]['docs'][file_name] += 1
    words[word]['doc_count'] = len(words[word]['docs'])
        

def read_file(words, file):
    tokens = regexp_tokenize(file.read().replace('\'', '').lower(), 
                            r'[\d\s,\.\?!"]\s*', gaps = True)
    stemmer = PorterStemmer()
    stemmed = [stemmer.stem(token) for token in tokens]
    
    file_name = os.path.basename(file.name)
    
    for word in stemmed:
        add_word(words, word, file_name)
        

files = glob.glob('input/*.txt')

files.sort()

index = {'doc_count' : len(files),
         'words' : {}}


for file in files:
    read_file(index['words'], open(file, 'r'))

with open('inverted-index.json', 'w') as out_file:
    json.dump(index, out_file)
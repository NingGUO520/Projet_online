import urllib2
import urllib
import zipfile
import tfidf
import os
import sys
from os.path import join
from bs4 import BeautifulSoup


#how many books to process
maxcount = int(sys.argv[1])

#file which contains the URL to mine (urls.md - don't save as txt file or program will attempt to read it during the analysis)
urlfile = sys.argv[2]

#Downloads the zip file, extracts the .txt file and deletes the .zip file to save space.
def downloadLink(url):
    tmp_fn = url.split("/")
    file_name = tmp_fn[len(tmp_fn)-1]

    #To make sure we're extracting only zip files that in the right format
    if ("zip" in url and "etext" not in url and "-8" not in url):
        urllib.urlretrieve(url, file_name)
        zip = zipfile.ZipFile(file_name)
        zip.extractall()
        os.remove(file_name)

        return 1
    else:
        return 0

#Reads in the urls.md file; if the url is of the format http://www.gutenberg.org/robot/harvest?filetypes[]=txt&langs[]=en, then we download that page and look through it for links to .zip files. Otherwise, we assume it is a regular link that we can download the book directly from.
def readInUrlFile(url_file):
    with open(url_file, 'rU') as u_file:
        cn = 0
        for line in u_file:
            if "robot" in line:
                response = urllib2.urlopen(line)
                html = response.read()
                soup = BeautifulSoup(html, 'html.parser')
                links = soup.find_all('a')
                for link in links:			
                    if cn < maxcount:
                        link_url = link.get('href')
                        if "offset" in link_url:
                            link_url = "http://www.gutenberg.org/robot/"+link_url
                            new_response = urllib2.urlopen(url)
                            new_html = new_response.read()
                            new_soup = BeautifulSoup(new_html, 'html.parser')
                            links.extend(new_soup.find_all('a'))
                        else:
                            cn += downloadLink(link_url)
            else:
                if cn < maxcount:
                    cn += downloadLink(line)

def main():
    readInUrlFile(urlfile)

if __name__ == "__main__":
    main()

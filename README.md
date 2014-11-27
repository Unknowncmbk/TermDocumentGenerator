TermDocumentGenerator
=====================

Java implementation of a Term-Document matrix generator used as a tool to compute the Latent Semantic Index of the data set.

Screenshot of the command prompt that gets displayed when the generator is first ran.
![](https://github.com/Unknowncmbk/TermDocumentGenerator/blob/master/console.png)

Example of the .csv file that is generated based on a results pulling unique words from various documents.
![](https://github.com/Unknowncmbk/TermDocumentGenerator/blob/master/termdoc.png)

We can load these matrices into matlab, and compute the SVD on these matrices. For example, if we want the top 6 query results from 12 documents, we can compute the average of the query term vectors and dot them with the query vector of each document.

Below is a screenshot of test query and the commands that were ran in matlab to compute the top 6 queries. In this specific example, the very top query is document 5. This is because the cosine similarity of document 5 is the closest to 1. Dissimilar documents have cosine similarities closer to 0.
![](https://github.com/Unknowncmbk/TermDocumentGenerator/blob/master/docrank.png)

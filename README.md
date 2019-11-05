# DBPanda
Projet de création d'une base de connaissance sur le thème de l'eSport

Le script JSONExtractor permet de récupérer les données via l'API fournie par PandaScore (https://pandascore.co) et de les stocker d'une manière adaptée.
Le script ModelGenerator permet lui de créer le modèle RDF que nous avons créé à partir de ces données. 
La ligne "model.write(out, "n-triples" );" permet de choisir le format voulu lors de la création du modèle (il faut pour cela aussi choisir un nom de fichier adapté : .xml pour RDF/XML, .nt pour n-triples et .ttl pour turtle).

Les modèles extraits sont disponbiles sur le repository, cependant les données JSON appartiennent à PandaScore et leur publication sous leur forme brute est interdite.


"The Customer is not in any way authorised to distribute, communicate or reproduce the raw data directly from the Database for commercial purposes or for consideration.In any event, none of the raw data obtained from the Database or obtainedthrough the use of the Site may be transferred as is, for free or for consideration, to any third party.

Only the results of the Customer’s work based directly or indirectly on the data contained in the Database and which has been the subject of specific processing by the Customer may, where appropriate, be marketed by it."
Source: Pandascore

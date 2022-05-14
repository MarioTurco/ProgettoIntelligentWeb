# ProgettoIntelligentWeb
Progetto Intelligent Web 2022 - ALC Reasoner con Lazy Unfolding 

# Manuale d’uso rapido

## Mario Turco N97000343 , Francesco Longobardi N97000344


## Contents

- Importazione
- Utilizzo
   - Caricare un Knowledge Base
   - Query
   - Stampa del tabluex
   - Posizione dei tabluex
- Knowledge Base macchina_fotografica.owl


## Importazione

Per importare il progetto è necessario scaricare Eclipse ed importare il progetto come ‘Maven Project’
come riportato in figura.

![Alt text](/Figures/1.png "Import")

## Utilizzo

### Caricare un Knowledge Base

Per caricare una knowledge base differente da quella caricata in automatico è necessario modificare il rigo
42 del file App.java, come riportato in figura.

![Alt text](/Figures/2.png "Import")

### Query

![Alt text](/Figures/3.png "Import")

È possibile caricare una query scrivendola da tastiera nel terminale all’avvio dell’applicazione.


### Stampa del tabluex

Prima di eseguire il programma si può scegliere se durante l’esecuzione verrà anche creato e
successivamente stampato il grafo del tabluex sotto forma di file .SVG e .RDF.
Se si vuole generare tali file andrà passato il parametro **“true”** alla funzione _‘executeAndPrintTime’_
alle righe 72 e 74 del file ‘App.java’, come riportato in figura:

![Alt text](/Figures/4.png "Import")

Se invece il parametro sarà settato a ‘false’ non verranno generati i file contenente i grafi.

### Posizione dei tabluex

Tutti i file generati dal programma si troveranno nella cartella “Progetto/graph/”

![Alt text](/Figures/5.png "Import")

Lazy corrisponde ai tablueax con il LazyUnboxing mentre NonEmpty corrisponde ai tableaux con tbox non
vuota senza LazyUnboxing.
Abbiamo scelto il formato .SVG per i tableaux poiché, essendo un file vettoriale, può essere zoomato senza
mai perdere qualità e di conseguenza si presta bene alla visualizzazione di immagini molto grandi.

![Alt text](/Figures/6.png "Import")

```
Figura 1 I label possono essere visualizzati sia passando il mouse sulla dicitura 'Label', sia cliccando sulla stessa._
```

## Knowledge Base macchina_fotografica.owl

![Alt text](/Figures/7.svg "Import")

Di seguito una definizione della KB presa da una esecuzione del programma:

```
##########KB##########
MacchinaFotografica ⊑ ∃ haSensore.Sensore 
Mirrorless ⊑MacchinaFotografica 
Reflex ⊑ ∃ haSpecchio.Specchio 
FullFrame ⊑Sensore 
Reflex ⊑MacchinaFotografica 
Mirrorless ⊑ ∀ haSpecchio.¬Specchio 
APSC ⊑Sensore 
haSpecchio Range: Specchio 
haSensore Range: Sensore 
MacchinaFotografica Disjoint Specchio 
Mirrorless Disjoint Reflex 
Sensore Disjoint Specchio 
APSC Disjoint FullFrame 
MacchinaFotografica Disjoint Sensore 
haSpecchio Domain: MacchinaFotografica 
haSensore Domain: MacchinaFotografica

```
```
Figura 2 - Le frecce nere solide rappresentano dominio e codominio delle relazioni, mentre le frecce blu solide rappresentano le
relazioni.
```


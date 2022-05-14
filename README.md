# ProgettoIntelligentWeb
Progetto Intelligent Web 2022 - ALC Reasone con Lazy Unfolding 

# Manuale d’uso rapido

## M ario Turco N9 7 0 0 0 343 , Francesco L o ngo bar di N9 7 0 0 0 3 44


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

## Utilizzo

### Caricare un Knowledge Base

Per caricare una knowledge base differente da quella caricata in automatico è necessario modificare il rigo
42 del file App.java, come riportato in figura.

### Query

È possibile caricare una query scrivendola da tastiera nel terminale all’avvio dell’applicazione.


### Stampa del tabluex

Prima di eseguire il programma si può scegliere se durante l’esecuzione verrà anche creato e
successivamente stampato il grafo del tabluex sotto forma di file .SVG e .RDF.
Se si vuole generare tali file andrà passato il parametro **“true”** alla funzione _‘executeAndPrintTime’_
alle righe 72 e 74 del file ‘App.java’, come riportato in figura:

Se invece il parametro sarà settato a ‘false’ non verranno generati i file contenente i grafi.

### Posizione dei tabluex

Tutti i file generati dal programma si troveranno nella cartella “Progetto/graph/”

Lazy corrisponde ai tablueax con il LazyUnboxing mentre NonEmpty corrisponde ai tableaux con tbox non
vuota senza LazyUnboxing.
Abbiamo scelto il formato .SVG per i tableaux poiché, essendo un file vettoriale, può essere zoomato senza
mai perdere qualità e di conseguenza si presta bene alla visualizzazione di immagini molto grandi.


_Figura 1 I label possono essere visualizzati sia passando il mouse sulla dicitura 'Label', sia cliccando sulla stessa._


## Knowledge Base macchina_fotografica.owl

Di seguito una definizione della KB presa da una esecuzione del programma:

```
##########KB##########
APSC ⊑Sensore
Reflex ⊑ ∃ haSpecchio.Specchio
FullFrame ⊑Sensore
Reflex ⊑MacchinaFotografica
Mirrorless ⊑ ∀ haSpecchio.¬Specchio
MacchinaFotografica ⊑ ∃ haSensore.Sensore
Mirrorless ⊑MacchinaFotografica
haSpecchio Range: Specchio
haSensore Range: Sensore
Mirrorless Disjoint Reflex
APSC Disjoint FullFrame
haSpecchio Domain: MacchinaFotografica
haSensore Domain: MacchinaFotografica
```
```
Figura 2 - Le frecce nere solide rappresentano dominio e codominio delle relazioni, mentre le frecce blu solide rappresentano le
relazioni.
```


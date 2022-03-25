# B_Compiler
Course: ASD-APP\
Docent: Matthijs de Jonge

Er is afgesproken met Matthijs de Jonge dat gebruik van Java 17 is toegestaan.

## Eisen
De opdracht voldoet aan de volgende eisen. Zie voor meer informatie [ASSIGNMENT.md](../ASSIGNMENT.md).

### Algemene eisen
* AL01
* AL02
* AL03
* AL04

### Parseren
* PA00
* PA01
* PA02
* PA03
* PA04
* PA05

### Checken
* CH00
* CH01
* CH02
* CH03
* CH04
* CH05
* CH06

### Transformeren
* TR01
* TR02

### Genereren
* GE01
* GE02

## Eigen uitbreiding
Naast de bovenstaande eisen, is ook een eigen uitbreiding toegevoegd. Hieronder welke uitbreidingen zijn toegevoegd en hoeveel punten is afgesproken dat iedere uitbreiding is.

Zie voor meer informatie de bestanden [level4.icss](src/main/test/resources/level4.icss), [level5.icss](src/main/test/resources/level5.icss) en [level6.icss](src/main/test/resources/level6.icss).

### Nested stylerules (5 punten)
```
p {
	color: #ff00ff;
	a {
		color: #123456;
	}
}
```
↓
```
p {
  color: #ff00ff;
}
p a {
  color: #123456;
}
```

Multi-selector nested stylerules werken ook:
```
p, div {
	color: #ff00ff;
	a, span {
		color: #123456;
	}
}
```
↓
```
p, div {
  color: #ff00ff;
}
p a, div a, p span, div span {
  color: #123456;
}
```

### Mixins (15 punten)
Mixins zijn functies diens 'body' als output terecht komt op de plaats van de 'mixin call'. Mixins kunnen argumenten hebben (en default values hiervoor, wat ze optioneel maakt).

Mixin namen moeten uniek zijn. Variabels in een hogere scope met dezelfde naam als argumenten of variabels binnen een mixin zijn niet bereikbaar, maar worden ook niet aangepast. Dit is een opzettelijke keuze geweest, omdat er anders een onderscheidende notatie geïmplementeerd moest worden voor het verwijzen naar de variabelen in hogere scopes (bijv `$SomeGlobalVar`), wat de taal nóg complexer zou maken en niet veel op zou leveren.

Een leuke functionaliteit is dat je kan verwijzen naar vorige argumenten in een mixin. Dit maakt het mogelijk om bijv. een tweede argument als default value twee keer de waarde van het eerste argument te geven.

Een mixin kan dezelfde 'onderdelen' bevatten als een stylerule:
* Variable declarations
* Declarations
* If-clauses
* Mixin calls
* Stylerules

#### Mixin zonder argumenten
```
Line() {
	width: 100%;
	height: 1px;
	background-color: #000000;
}

#line {
	Line();
}
```
↓
```
#line {
  width: 100%;
  height: 1px;
  background-color: #000000;
}
```

#### Mixin met verplichte argumenten
```
Button(Width, Height, Color) {
    width: Width;
    height: Height;
    color: Color;
}

#button-a, #button-b {
    Button(80px, 40px, #00ff00);
}
```
↓
```
#button-a, #button-b {
  width: 80px;
  height: 40px;
  color: #00ff00;
}
```

#### Mixin met optionele argumenten
```
Button(Width, Height, Color) {
    width: Width;
    height: Height;
    color: Color;
}

FilledButton(Width, Height, FgColor: #000000, BgColor: FgColor) {
    Button(Width, Height, FgColor);
    background-color: BgColor;
}

#filled-button {
    FilledButton(100px, 50px, #444444);
}
```
↓
```
#filled-button {
  width: 100px;
  height: 50px;
  color: #444444;
  background-color: #444444;
}
```

#### Mixin met argumenten die naar vorige argumenten verwijzen
```
SpecialParagraph(LinkColor: LinkColor) {
    p {
        width: ParWidth;
        a {
            color: LinkColor;
        }
    }
}

#highlighted-section {
    background-color: #000000;
    SpecialParagraph(#123546);
}
```
↓
```
#highlighted-section {
  background-color: #000000;
}
#highlighted-section p {
  width: 500px;
}
#highlighted-section p a {
  color: #123546;
}
```

### Multi-selector (0 punten, eigen initiatief)
```
p, a {
	width: 1px;
}
```
↓
```
p, a {
  width: 1px;
}
```

## Tests
Er zijn unit tests voor de nieuwe 'levels' (elk van de bovenstaande uitbreidingen) van de parser toegevoegd, en ook voor de checker, HANLinkedList en HANStack. De 'levels' zijn ook toegevoegd aan het menu voor de GUI, en er zijn 'integratietests' voor toegevoegd (JUnit-tests die de stappen in de pipeline aflopen: parse → check → transform → generate).

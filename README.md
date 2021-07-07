## Az alkalmazással kapcsolatos követelmények:

#### Általános követelmények a forráskóddal és az alkalmazással kapcsolatban

* A 'Ticket service>' prompt kerül kiírásra, amint az alkalmazás készen áll arra, hogy bemenetet fogadjon a felhasználótól
* Az alkalmazás a standard kimenetre ír és a standard bemenetről olvas
 Az alkalmazás által használt adatbázis Spring profilok segítségével módosítható.
    * Alapértelmezetten az alkalmazás egy olyan adatbázist használ, amely az alkalmazás futásai között is megőrzi az állapotát
    * A `ci` profil esetén az alkalmazás beágyazott in-memory adatbázist használ 
    * Legalább 70%-os unit teszt branch és line coverage az egész `ticket-service` modulra vonatkoztatva.
#### Az alkalmazás által támogatott parancsok

* Az alkalmazás állapotát egy interaktív parancssor segítségével lehet módosítani.
* A parancssorból az `exit` parancs segítségével bármikor ki lehet lépni, ekkor a 
program futása leáll.
* Az egyes parancsokhoz lehetséges több szóból álló paraméter megadása úgy, 
ha az adott paramétert idézőjelek közé írjuk. Például a következő parancs 
a _Spirited Away_ című film létrehozására használható.
```
create movieEntity "Sprited Away" animation 125
```

##### Admin accounttal kapcsolatos parancsok

###### Admin account és bejelentkezés

* Az alkalmazás indításakor létezik egy adminisztrátor account.
    * A felhasználónév legyen 'admin', a jelszó szintén 'admin'.
* A következő parancs lehetőséget nyújt az adminisztátornak arra, hogy 
bejelentkezzen
```
sign in privileged <felhasználónév> <jelszó>
```
* Sikertelen bejelentkezés esetén a parancs kimenete a következő:
```
Login failed due to incorrect credentials
```
* Sikeres bejelentkezés esetén hozzáférhetővé
vállnak az adminisztrációs parancsok (lásd később)

###### Az adminisztrátor ki tud jelentkezni

* A következő parancs segítségével az adminisztrátor ki tud jelentkezni
```
sign out
```
* A kijelentkezés után ne legyenek hozzáférhetőek az adminisztrációs parancsok,
amíg az adminisztrátor felhasználó újra be nem jelentkezik.

###### Az adminisztrátor account információi lekérdezhetőek

* A következő paranccsal lekérdezhető az éppen bejelentkezett
account típusa és állapota.
```
describe account
```
* Bejelentkezett adminisztrátor esetén a parancs kimenete:
```
Signed in with privileged account '<felhasználónév>'
```
* Amennyiben a felhasználó nincs bejelentkezve, a parancs kimenete:
```
You are not signed in
```

##### A vetített filmekről szóló adatok kezelése

###### Filmek létrehozása

* A következő paranccsal új film hozható létre.
```
create movieEntity <film címe> <műfaj> <vetítés hossza percben>
``` 
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.
* A film címe azonosítja a filmet.

Például:
```
create movieEntity Sátántangó drama 450
```

###### Filmek módosítása

* A következő paranccsal egy már meglévő filmet módosíthatunk.
```
update movieEntity <film címe> <műfaj> <vetítés hossza percben>
```
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.
* A film címe azonosítja a filmet

###### Filmek törlése

* A következő paranccsal egy már meglévő filmet törölhetünk.
```
delete movieEntity <film címe>
```
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.

###### Filmek lekérdezése

* A következő paranccsal a meglévő filmeket kérdezhetjük le
```
list movies
``` 
* A parancs kimenete a következő, ha még nincs film elmentve:
```
There are no movies at the moment
```
* Ha van film elmentve, akkor a parancs kimenetében szerepelnie kell az összes
filmnek. Minden sor egy-egy filmet ír le és a következőképpen épül fel.
```
<Film címe> (<műfaj>, <vetítés hossza percben> minutes)
```
* Ez a parancs elérhető bármely bejelentkezett vagy nem bejelentkezett felhasználónak.

##### A termekről szóló információ kezelése

###### Terem létrehozása

* A következő parancs lehetőséget biztosít vetítő termek létrehozására.
```
create room <terem neve> <széksorok száma> <szék oszlopok száma>
```
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.
* A terem neve azonosítja a termet

###### Terem módosítása

* A következő parancs lehetőséget biztosít már meglévő vetítő termek módosítására.
```
update room <terem neve> <széksorok száma> <szék oszlopok száma>
```
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.
* A terem neve azonosítja a termet

###### Terem törlése

* A következő parancs lehetővé teszi egy már meglévő vetítő terem törlését.
```
delete room <terem neve>
```
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.

###### Termek listázása

* A következő parancs lehetővé teszi a termek listázását
```
list rooms
```
* Amennyiben még nincs terem elmentve, a parancs kimenete a következő:
```
There are no rooms at the moment
```
* Amennyiben már van terem elmentve, akkor a parancs kimenetében 
szerepelnie kell minden teremnek. Minden sor egy termet ír le, az
egyes sorok formátuma a következő:
```
Room <terem neve> with <székek száma> seats, <széksorok száma> rows and <szék oszlopok száma> columns 
``` 
* Ez a parancs elérhető bármely bejelentkezett vagy nem bejelentkezett felhasználónak.

##### A filmvetítésekről szóló információk kezelése

###### Vetítés létrehozása

* A következő parancs lehetőséget biztosít egy már létező film létező teremben történő
vetítésének létrehozására egy adott időpontban.
```
create screening <film címe> <terem neve> <vetítés kezdetének dátuma és ideje, YYYY-MM-DD hh:mm formátumban>
```
Például, ha a Pedersoli terem és a Spirited Away film léteznek, akkor egy vetítés a 2021-03-14 16:00 
időpontban létrehozható a következőképpen:
```
create screening "Spirited Away" Pedersoli "2021-03-14 16:00"
```
* Nem hozható létre a vetítés akkor, ha:
    * A vetítés időtartama bele esik egy másik, azonos teremben történő másik vetítés időtartamába. 
    A kimenet ebben az esetben:
    ```
    There is an overlapping screening
    ``` 
    * A vetítés időtartama bele esik egy másik, azonos teremben történő vetítés utáni 10 perces szünetbe (amely lehetőséget
    biztosít például a terem előkészítésére a következő vetítésre). A kimenet ebben az esetben:
    ```
    This would start in the break period after another screening in this room
    ```
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.
* Egy vetítést a film címe, a terem neve és a vetítés kezdetének dátuma és ideje azonosít

###### Vetítés törlése

* A következő parancs lehetőséget biztosít egy már létező vetítés törlésére:
```
delete screening <film címe> <terem neve> <vetítés kezdetének dátuma és ideje, YYYY-MM-DD hh:mm formátumban>
```
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.

###### Vetítések listázása

* A következő parancs lehetőséget biztosít már meglévő vetítések listázására
```
list screenings
```
* Amennyiben nincs mentett vetítés, a kimenet a következő:
```
There are no screenings
```
* Amennyiben már van mentett vetítés, akkor a kimenet minden vetítést tartalmaz.
A kimenet egy-egy sora egy-egy vetítés adatait tartalmazza a következő formátumban:
```
<A film címe> (<műfaj>, <vetítés ideje percben> minutes), screened in room <terem neve>, at <vetítés kezdetének dátuma és ideje, YYYY-MM-DD hh:mm formátumban>
```
Például:
```
Sátántangó (drama, 450 minutes), screened in room Pedersoli, at 2021-03-15 11:00
```

##### Regisztráció és bejelentkezés nem adminisztrátor felhasználók számára

###### Regisztráció nem adminisztrátor felhasználó számára

* A következő parancs lehetőséget biztosít nem adminisztrátori account
létrehozására.
```
sign up <felhasználónév> <jelszó>
```
* A parancs elérhető nem bejelentkezett és bejelentkezett felhasználóknak
* A felhasználót a felhasználónév azonosítja

###### Bejelentkezés nem adminisztrátor felhasználók számára

* A következő parancs lehetőséget biztosít már meglévő, nem adminisztrátori
accountba történő bejelentkezéshez.
```
sign in <felhasználónév> <jelszó>
```
* Sikeres bejelentkezés esetén a kimenet tetszőleges lehet, a felhasználó
  a bejelentkezés után eléri a bejelentkezett felhasználók számára elérhető
  parancsokat.
* Sikertelen bejelentkezés esetén a kimenet a következő:
```
Login failed due to incorrect credentials
```

###### Account adatainak lekérdezése bejelentkezett, nem adminisztrátor felhasználók számára

* A következő parancs bejelentkezett, nem adminisztrátor felhasználók esetén is
az accountra vonatkozó adatok lekérdezésére szolgál
```
describe account
```
* A parancs kimenete bejelentkezett, nem adminisztrátor felhasználó esetén, ha még nem foglalt
jegyet:
```
Signed in with account '<felhasználónév>'
You have not booked any tickets yet
```
* A parancs kimenete bejelentkezett, nem adminisztrátor felhasználó esetén, ha már foglalt
jegyet:
    ```
    Signed in with account '<felhasználónév>'
    Your previous bookings are
    Seats <a vetítésre foglalt ülések listája, ", "-el elválasztva, egy-egy ülés "(<sor>,<oszlop>) formátumú> on <film címe> in room <terem neve> starting at <vetítés kezdetének ideje YYYY-MM-DD hh:mm formátumban> for <a foglalás ára> HUF
    ```
    * Minden már meglévő foglaláshoz létezik egy, a foglalást leíró sor a fenti formátumban.
    * A foglalásra vonatkozó szabályokról (pl. az ár kiszámítása) bővebben olvashatsz a következő szekciókban.
    * Például:
    ```
    Signed in with account 'sanyi'
    Your previous bookings are
    Seats (5,5), (5,6) on Sátántangó in room Pedersoli starting at 2021-03-15 10:45 for 3000 HUF
    ```
* A parancs kimenete más esetekben a kettes jegyhez szükséges követelmények között van leírva.

##### Jegyfoglalás

###### Foglalás létrehozása
* A következő parancs lehetőséget biztosít egy már meglévő vetítésre egy vagy több ülőhelyre
jegyet foglalni.
```
book <film címe> <terem neve> <vetítés kezdetének ideje YYYY-MM-DD hh:mm formátumban> <a lefoglalandó ülőhelyek listája, szóközzel elválasztva, egy-egy ülőhely "<sor>,<oszlop>" formátumú> 
```
Például:
```
book Sátántangó Pedersoli "2021-03-15 10:45" "5,5 5,6"
```
* A foglalás sikertelen akkor, ha 
    * A megjelölt ülőhelyek közül legalább egy már foglalt. Ekkor a parancs kimenete
    ```
    Seat <ülőhely> is already taken
    ```
    ahol az ülőhely a listában megadottak közül az első olyan, amely nem foglalható
    * A megjelölt ülőhelyek valamelyike nem létezik az adott teremben. Ekkor a parancs kimenete
    ```
    Seat <ülőhely> does not exist in this room
    ```
    ahol az ülőhely a listában megadottak közül az első olyan, amely nem foglalható a hiba miatt.
* Amennyiben a foglalás sikeres, a parancs kimenete a következő
    ```
    Seats booked: <a lefoglalt ülőhelyek listája, ", "-el elválasztva, egy-egy ülőhely "(<sor>,<oszlop>)" formátumú>; the price for this booking is <a jegy ára> HUF 
    ```
    * A jegy ára 1500 HUF / ülőhely / vetítés. Az ötös érdemjegyhez tartozó követelményekben 
    további szabályok kerülnek bevezetésre.
* Ez a parancs csak bejelentkezett, nem adminisztrátor felhasználóknak elérhető

##### Árazással kapcsolatos funkcionalitás

###### Az alapár megváltoztatása
* Egy foglalás alapára 1500 HUF / ülőhely / vetítés. 
A foglalás ára (/ülőhely/vetítés) kiszámítható a foglalás alapárának 
és a foglalásra vonatkozó árkomponensek összegzésével. Az árkomponensek
leírását lásd később. 
* A következő parancs a foglalás alapárának megváltoztatását teszi lehetővé
```
update base price <új alapár>
```
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.
* A parancs nem változtathatja meg a már meglévő foglalások árát. 
    * ezek a `describe account` paranccsal kérhetőek 
    le az éppen bejelentkezett felhasználóhoz.
* A parancs hatására az új foglalások árának számításakor már az új alapár
kerül felhasználásra.

###### Új árkomponens létrehozása
* Egy árkomponens egy pozitív vagy negatív fix összeg, amely az alapárhoz adódik hozzá.
Ennek segítségével vállnak megvalósíthatóvá teremhez, filmhez vagy vetítéshez használható felárak
vagy kedvezmények.
* A következő parancs használható az árkomponensek létrehozásához:
```
create price component <árkomponens neve> <árkomponens összege>
```
* Az árkomponens neve azonosítja az árkomponenst
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.

###### Árkomponens teremhez történő csatolása
* A következő parancs lehetővé teszi egy létező árkomponens egy létező teremhez történő csatolását.
Miután megtörtént, a teremben történő összes vetítés ára tartalmazza az árkomponenst.
    * Például ha egy vetítésre, két ülőhelyre szeretnénk jegyet foglalni, amely csak az 1500 Ft/ülőhely/vetítés 
    alapárat tartalmazza, akkor a foglalás teljes ára 2 * 1500 Ft, azaz 3000 Ft. Amennyiben ahhoz a teremhez,
    ahol a vetítés történik csatolva van egy 500 Ft-s árkomponens, akkor a fenti foglalás ára
    2 * (500 Ft + 1500 Ft), tehát 4000 Ft
```
attach price component to room <árkomponens neve> <terem neve>
```
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.
* A parancs nem változtathatja meg a már meglévő foglalások árát. 

###### Árkomponens filmhez történő csatolása
* A következő parancs lehetővé teszi egy létező árkomponens egy létező filmhez történő csatolását.
Miután megtörtént, a film összes vetítése esetén az ár tartalmazza az árkomponenst.
```
attach price component to movieEntity <árkomponens neve> <film címe>
```
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.
* A parancs nem változtathatja meg a már meglévő foglalások árát. 

###### Árkomponens filmhez vetítéshez történő csatolása
* A következő parancs lehetővé teszi egy létező árkomponens egy létező vetítéshez történő csatolását.
Miután megtörtént, a vetítés ára tartalmazza az árkomponenst.
```
attach price component to screening <árkomponens neve> <film címe> <terem neve> <vetítés kezdetének dátuma YYYY-MM-DD hh:mm formátumban>
```
* A parancs adminisztrációs parancs, így csak bejelentkezett 
adminisztrátor felhasználó számára elérhető.
* A parancs nem változtathatja meg a már meglévő foglalások árát. 

###### Foglalás árának lekérdezése
* A következő parancs lehetővé teszi azt, hogy lekérdezhessük egy foglalás árát anélkül, hogy a foglalás megtörténne
```
show price for <film címe> <terem neve> <vetítés kezdetének dátuma YYYY-MM-DD hh:mm formátumban> <ülőhelyek listája szóközzel elvállasztva, az egyes ülőhelyek "<sor>,<oszlop>" formátumúak>
```
Például:
```
show price for Sátántangó Pedersoli "2021-03-15 10:45" "10,5 11,5"
```
* A parancs kimenete:
```
The price for this booking would be 3000 HUF
```
* A parancs elérhető bármely bejelentkezett vagy nem bejelenetkezett felhasználónak.

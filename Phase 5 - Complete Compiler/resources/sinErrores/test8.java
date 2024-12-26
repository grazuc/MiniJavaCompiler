///true&false&true&false&false&false&exitosamente

class MainModule{
    static void main(){

        var bool = ((4+ 10 + 33) > 12);
        System.printBln(bool);
        bool = ((99*15) == 23);
        System.printBln(bool);

        System.printBln((97>=12));
        System.printBln(!true);

        bool = ((4/2) != 2);
        System.printBln(bool);

        bool = ((4+ 10 + 33) > 12) || ((99*15) == 23) && (97>=12) || !true && ((4/2) != 2);
        System.printBln(bool);

    }
}
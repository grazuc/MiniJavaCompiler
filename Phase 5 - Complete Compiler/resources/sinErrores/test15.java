///Valor actual de iteracion: 0&El numero (0) es par!!&Valor actual de iteracion: 1&Valor actual de iteracion: 2&El numero (2) es par!!&Valor actual de iteracion: 3&Valor actual de iteracion: 4&El numero (4) es par!!&Valor actual de iteracion: 5&Valor actual de iteracion: 6&El numero (6) es par!!&Valor actual de iteracion: 7&Valor actual de iteracion: 8&El numero (8) es par!!&Valor actual de iteracion: 9&Valor actual de iteracion: 10&El numero (10) es par!!&exitosamente


class MainModule{
    static void main(){

        var i = 0;

        while( i <= 10 ){
            System.printS("Valor actual de iteracion: ");
            System.printIln(i);

            if( i % 2 == 0 ){
                System.printS("El numero (");
                System.printI(i);
                System.printSln(") es par!!");
            }
            i = i + ((i + 1) - i);
        }
    }
}
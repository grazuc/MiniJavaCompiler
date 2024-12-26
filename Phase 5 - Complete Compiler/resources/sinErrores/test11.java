///7&true&101&99&1&exitosamente


class MainModule{
    static void main(){
        var x = new A(7, true);
        System.printIln(x.enteroX());
        System.printIln(x.enteroA());
        System.printIln(x.enteroW());
    }
}

class A extends X{

    public A(int i, boolean b){
        System.printIln(i);
        System.printBln(b);
    }

    int enteroA(){
        return 99;
    }
}

class X extends W{
    int enteroX(){
        return 101;
    }
}

class W{
    int enteroW(){
        return 1;
    }
}
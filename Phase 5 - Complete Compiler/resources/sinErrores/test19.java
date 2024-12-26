///Hola&Mundo&!!!&exitosamente


class A{
    String at1;
    static void main(){
        var a = "Hola";
        new A().init(a).m1();
        {
            {
                var b = "asd";
                var c = "dsa";
            }
        }
        {
            var b = "Mundo";
            var c = "!!!";
            new A().init(b).m1();
            new A().init(c).m1();
        }
    }

    A init(String i){
        this.at1 = i;
        return this;
    }

    A m1(){
        System.printSln(this.at1);
        return this;
    }
}
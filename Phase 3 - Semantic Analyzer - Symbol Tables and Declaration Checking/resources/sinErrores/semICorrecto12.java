
//test para ver si se heredan los metodos correctamente

class A implements C {
    static void main(){}

    A metodo_h(){}
    int otro_metodo_h(){}
    String metodoC() {}
}

interface H {
    A metodo_h();
    int otro_metodo_h();
}

class B extends A  {

}

interface C{
    String metodoC();
}

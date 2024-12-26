///12345&98765&exitosamente


class A{
    String at1;
    static void main(){
        var a = 54321;
        var b = 56789;
        System.printIln(reverse(a));
        System.printIln(reverse(b));
    }

    static int reverse(int i){
        return reverseAux(i).reversedNumber;
    }

    static Pair reverseAux(int i){
        if(i < 10) {
            var ret = new Pair();
            ret.reversedNumber = i;
            ret.multiplier = 10;
            return ret;
        }
        else {
            var ret = reverseAux(i / 10);
            ret.reversedNumber = (i % 10) * ret.multiplier + ret.reversedNumber;
            ret.multiplier = ret.multiplier * 10;
            return ret;
        }
    }
}

class Pair{
    int reversedNumber;
    int multiplier;
}
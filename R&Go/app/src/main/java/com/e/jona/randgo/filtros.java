package com.e.jona.randgo;

public class filtros {

    private int n;
    private double []arr;

    private float Pp = 0.0f;                  //prediction next covariance
    private float G = 0.0f;                   //kalman gain
    private float P = 1.0f;
    private float Xp = 0.0f;
    private float Zp = 0.0f;
    private float Xk = 0.0f;       //estado anterior predicto xk-1
    private double yanterior_ema=0f;
    private double xanterior_fir=0f;
    private double yanterior_iir=0f;
    private double alfa=0;
    private int coun=1;

    public  filtros(int op)
    {
        this.n=op;
        this.arr=new double[op];
        for(int k=0; k<op; k++)
        {
            this.arr[k]=0;
        }
    }

    public void encerarmediamovil()
    {
        for(int k=0; k<n; k++)
        {
            arr[k]=0;
        }
    }

    public double f_media_movil(double medida)
    {

        for(int k=0; k<n-1; k++)
        {
            arr[k]=arr[k+1];
        }
        arr[n-1]=medida;

        double aux=0;
        for(int k=0; k<n; k++)
        {
            aux=aux+arr[k];
        }

        if(coun<n)
        {
            aux=aux/coun;
            coun++;
        }
        else{
            aux=aux/n;
        }
        return aux;
    }

    public double f_ema(double x)
    {
        double y=alfa*x+(1-alfa)*yanterior_ema;
        yanterior_ema=y;
        return y;
    }
    public void set_alpha_ema(double alfa){
        this.alfa=alfa;

    }


    public double f_fir(double x)
    {
        xanterior_fir=x;
        return 0.5*x+0.5*xanterior_fir;

    }

    public  double iir(double x)
    {
        double y=0.5*x+0.5*yanterior_iir;
        yanterior_iir=y;
        return y;
    }

    public  void encerar_kalman()
    {
        // kalman variables
        //   float R = 1.12184278324081E-05;  // covarianza del ruido en las medidas
        //   float Q = 1e-8;                  //covarianza del ruido en el proceso
        Pp = 0.0f;                  //prediction next covariance
        G = 0.0f;                   //kalman gain
        P = 1.0f;
        Xp = 0.0f;
        Zp = 0.0f;
        Xk = 0.0f;       //estado anterior predicto xk-1
    }
    public  double kalman(double medida, float R, float Q)
    {
        Xp = Xk;
        Pp = P + Q;

        G = Pp/(Pp + R);    // kalman gain
        P = (1-G)*Pp;

        Xk = Xp+G*((float)medida-Xp);   // the kalman estimate of the sensor voltage
        return Xk;
    }


}

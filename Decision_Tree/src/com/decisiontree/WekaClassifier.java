package com.decisiontree;

public class WekaClassifier {

  public static double classify(Object[] i)
    throws Exception {

    double p = Double.NaN;
    p = WekaClassifier.N23e4ef930(i);
    return p;
  }
  static double N23e4ef930(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (i[0].equals("  end_rack")) {
      p = 0;
    } else if (i[0].equals("  cd_spec")) {
    p = WekaClassifier.Nb2cae4e1(i);
    } else if (i[0].equals("  std_rack")) {
    p = WekaClassifier.N2267889d2(i);
    } 
    return p;
  }
  static double Nb2cae4e1(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() <= 80.0) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() > 80.0) {
      p = 1;
    } 
    return p;
  }
  static double N2267889d2(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (i[3].equals("FALSE")) {
      p = 1;
    } else if (i[3].equals("TRUE")) {
      p = 0;
    } 
    return p;
  }
}

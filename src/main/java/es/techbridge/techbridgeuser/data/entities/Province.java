package es.techbridge.techbridgeuser.data.entities;

import java.util.Arrays;
import java.util.Optional;

public enum Province {
    ALAVA("01"),
    ALBACETE("02"),
    ALICANTE("03"),
    ALMERIA("04"),
    AVILA("05"),
    BADAJOZ("06"),
    BALEARES("07"),
    BARCELONA("08"),
    BURGOS("09"),
    CACERES("10"),
    CADIZ("11"),
    CASTELLON("12"),
    CIUDAD_REAL("13"),
    CORDOBA("14"),
    CORUNA("15"),
    CUENCA("16"),
    GIRONA("17"),
    GRANADA("18"),
    GUADALAJARA("19"),
    GIPUZKOA("20"),
    HUELVA("21"),
    HUESCA("22"),
    JAEN("23"),
    LEON("24"),
    LLEIDA("25"),
    LUGO("27"),
    MADRID("28"),
    MALAGA("29"),
    MURCIA("30"),
    NAVARRA("31"),
    OURENSE("32"),
    ASTURIAS("33"),
    PALENCIA("34"),
    LAS_PALMAS("35"),
    PONTEVEDRA("36"),
    SALAMANCA("37"),
    SANTA_CRUZ_DE_TENERIFE("38"),
    CANTABRIA("39"),
    SEGOVIA("40"),
    SEVILLA("41"),
    SORIA("42"),
    TARRAGONA("43"),
    TERUEL("44"),
    TOLEDO("45"),
    VALENCIA("46"),
    VALLADOLID("47"),
    BIZKAIA("48"),
    ZAMORA("49"),
    ZARAGOZA("50"),
    CEUTA("51"),
    MELILLA("52");

    private final String code;

    Province(String code) {
        this.code = code;
    }

    public static Optional<Province> fromCode(String code) {
        return Arrays.stream(values())
                .filter(province -> province.code.equals(code))
                .findFirst();
    }

    public String getCode() {
        return code;
    }

}


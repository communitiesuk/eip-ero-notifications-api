package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.AddressDto
import uk.gov.dluhc.notificationsapi.dto.EroContactDetailsDto

fun anEnglishEroContactDetails(): EroContactDetailsDto =
    EroContactDetailsDto(
        name = "Gwynedd Council Elections",
        phoneNumber = "01766 771000",
        website = "https://www.gwynedd.llyw.cymru/en/Council/Contact-us/Contact-us.aspx",
        emailAddress = "TrethCyngor@gwynedd.llyw.cymru",
        address = AddressDto(
            property = "Gwynedd Council Headquarters",
            street = "Shirehall Street",
            locality = null,
            town = "Caernarfon",
            area = "Gwynedd",
            postcode = "LL55 1SH",
        ),
    )

fun aWelshEroContactDetails(): EroContactDetailsDto =
    EroContactDetailsDto(
        name = "Etholiadau Cyngor Gwynedd",
        phoneNumber = "01766 771000",
        website = "https://www.gwynedd.llyw.cymru/cy/Cyngor/Cysylltu-%c3%a2-ni/Cysylltu-%c3%a2-ni.aspx",
        emailAddress = "TrethCyngor@gwynedd.llyw.cymru",
        address = AddressDto(
            property = "Pencadlys Cyngor Gwynedd",
            street = "Stryd y Jêl",
            locality = null,
            town = "Caernarfon",
            area = "Gwynedd",
            postcode = "LL55 1SH",
        ),
    )

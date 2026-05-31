package com.example.data

data class AffiliateResource(
    val id: String,
    val title: String,
    val description: String,
    val affiliateUrl: String,
    val category: String,
    val promoCode: String,
    val discountText: String,
    val relevanceTabName: String, // "Dashboard", "Filings", "Policy", "Misconduct"
    val resourceType: String, // "Hardware", "Software", "Legal Service", "Guides"
    val rewardCredits: Int = 10,
    val benefitStatement: String
)

object AffiliateResourcesRegistry {
    val INDEXED_RESOURCES = listOf(
        AffiliateResource(
            id = "nolo_michigan_guides",
            title = "Nolo Pro Se Litigation Guides (Michigan Edition)",
            description = "Master Michigan rules, litigation strategies, and cross-examinations. Prepares low-income litigants to represent themselves standard-for-standard against high-priced corporate or public attorneys.",
            affiliateUrl = "https://www.nolo.com/products/michigan-forms-and-guides?aff=prosecasevault",
            category = "Legal Guides & Checklists",
            promoCode = "PROSEVAULT20",
            discountText = "20% OFF ALL LITIGATION GUIDES",
            relevanceTabName = "Policy",
            resourceType = "Guides",
            rewardCredits = 15,
            benefitStatement = "Includes official SCAO MCR court-room companion templates and strategies."
        ),
        AffiliateResource(
            id = "proton_encrypted_vault",
            title = "Proton Drive Sovereign Shield Cloud Storage",
            description = "E2E encrypted, Swiss-hosted cloud catalog. Keeps child protection recordings, witness photos, and personal journals fully immune to illegal administrative seizures or device confiscations.",
            affiliateUrl = "https://proton.me/drive?aff=casevaultshield",
            category = "Evidence Anti-Interception Vault",
            promoCode = "SOVEREIGNSHIELD",
            discountText = "500MB FREE + 25% OFF ANNUAL PLANS",
            relevanceTabName = "Filings",
            resourceType = "Software",
            rewardCredits = 25,
            benefitStatement = "Zero-knowledge encryption ensures neither state actors nor Proton can view your case. Direct linkable upload speeds."
        ),
        AffiliateResource(
            id = "fujitsu_scansnap_ix1600",
            title = "Fujitsu ScanSnap iX1600 Duplex PDF Scanner",
            description = "Convert stacks of discovery requests, state subpoenas, and certified dockets directly into fully-searchable PDFs. Directly indexes into local CaseVault folders in one-tap.",
            affiliateUrl = "https://www.amazon.com/dp/B08P5F9D6C?tag=prosecasevault-20",
            category = "Evidence Hardware Digitizers",
            promoCode = "CASEVAULTSCAN",
            discountText = "$35 INSTANT REBATE CODE",
            relevanceTabName = "Filings",
            resourceType = "Hardware",
            rewardCredits = 40,
            benefitStatement = "Ultra-fast legal duplex feeding prevents missed pages, optimizing audit readiness for MCR submission rules."
        ),
        AffiliateResource(
            id = "boblov_hd_bodycam",
            title = "BOBLOV D1 HD Sovereign-Proof Body-Worn Camera",
            description = "Stealth rotatable body camera with night-vision. Capture audio-visual evidence of agency caseworkers, private investigators, or municipal inspectors entering settings without consent.",
            affiliateUrl = "https://www.amazon.com/dp/B08LGTZXYY?tag=prosecasevault-20",
            category = "Misconduct Recordkeeping Tech",
            promoCode = "RECORDSAFE15",
            discountText = "15% OFF AT CHECKOUT VIA LINK",
            relevanceTabName = "Misconduct",
            resourceType = "Hardware",
            rewardCredits = 30,
            benefitStatement = "Preserves objective high-fidelity recording of bad-faith actors violating constitutional protections."
        ),
        AffiliateResource(
            id = "phomemo_portable_printer",
            title = "Phomemo M08F Inkless Portable Courtroom Printer",
            description = "A lightweight thermal file printer. Ideal for parents and pro se advocates who need to immediately print physical affidavits, amended motions, or SCAO fee waivers directly block-side in courthouses.",
            affiliateUrl = "https://www.amazon.com/dp/B0B68XSM8R?tag=prosecasevault-20",
            category = "Courtroom Portables",
            promoCode = "PHOMEMOPROSE",
            discountText = "10% INKLESS DISCOUNT",
            relevanceTabName = "Dashboard",
            resourceType = "Hardware",
            rewardCredits = 20,
            benefitStatement = "Allows pro se litigants to respond instantly to last-minute bad-faith document dumps without stepping out."
        ),
        AffiliateResource(
            id = "rocket_lawyer_oncall",
            title = "Rocket Lawyer Pro Se Document Builder & Attorney On-Call",
            description = "Draft state and county specialized motions, subpoenas, and discovery sheets. Includes dynamic local review checklists with localized certified legal specialists.",
            affiliateUrl = "https://www.rocketlawyer.com/?bt=casevault-partner",
            category = "Attorney Verified Checklists",
            promoCode = "ROSEVAULTFREE",
            discountText = "7-DAY PREMIUM ACCESS TRIAL",
            relevanceTabName = "Dashboard",
            resourceType = "Legal Service",
            rewardCredits = 15,
            benefitStatement = "Reduces the barrier of UPL (Unauthorized Practice of Law) by letting licensed advocates proofread layout concepts before submission."
        ),
        AffiliateResource(
            id = "us_legal_forms_catalog",
            title = "US Legal Forms Official Michigan SCAO Multi-Pack",
            description = "Get comprehensive state-compliant bundles of custody modifications, protection orders, specific property claims, and MCR litigation forms. Ideal for complex multi-agency cases.",
            affiliateUrl = "https://www.uslegalforms.com/catalog/michigan-forms/?aff=casevaultscao",
            category = "Pre-Formatted State Bundles",
            promoCode = "MICHFORM10",
            discountText = "10% RETAINER REDUCTION CODE",
            relevanceTabName = "Filings",
            resourceType = "Guides",
            rewardCredits = 20,
            benefitStatement = "Fully pre-compiled bundles eliminate layout rejections by municipal or county circuit clerks of court."
        ),
        AffiliateResource(
            id = "vantrue_stealth_dashcam",
            title = "Vantrue N4 Pro Triple Stealth Loop Dashcam",
            description = "Three-channel continuous video-audio recorder with GPS coordinate tracking. Secures automated evidence of police stops, harassment, or private agency surveillance on parents.",
            affiliateUrl = "https://www.amazon.com/dp/B0C3M1Z9V8?tag=prosecasevault-20",
            category = "Home & Auto Protection Sensors",
            promoCode = "VANSTEALTH",
            discountText = "EXTRA $20 COUPON ON PRODUCT PAGE",
            relevanceTabName = "Misconduct",
            resourceType = "Hardware",
            rewardCredits = 35,
            benefitStatement = "A robust deterrent establishing physical agency overreach, trespass, and surveillance tactics."
        )
    )
}

package com.foodbridge.init;

import com.foodbridge.entity.*;
import com.foodbridge.entity.enums.*;
import com.foodbridge.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FoodDonationRepository foodDonationRepository;
    private final MaterialDonationRepository materialDonationRepository;
    private final CashCampaignRepository cashCampaignRepository;
    private final CashTransactionRepository cashTransactionRepository;
    private final CommunityNeedRepository communityNeedRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${foodbridge.demo-data.enabled:true}")
    private boolean demoDataEnabled;

    public DataInitializer(UserRepository userRepository,
                           FoodDonationRepository foodDonationRepository,
                           MaterialDonationRepository materialDonationRepository,
                           CashCampaignRepository cashCampaignRepository,
                           CashTransactionRepository cashTransactionRepository,
                           CommunityNeedRepository communityNeedRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.foodDonationRepository = foodDonationRepository;
        this.materialDonationRepository = materialDonationRepository;
        this.cashCampaignRepository = cashCampaignRepository;
        this.cashTransactionRepository = cashTransactionRepository;
        this.communityNeedRepository = communityNeedRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed Admin user always if not exists
        if (!userRepository.existsByEmail("admin@foodbridge.org")) {
            User admin = new User();
            admin.setName("FoodBridge Administrator");
            admin.setEmail("admin@foodbridge.org");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setPhone("+91 98401 12233");
            admin.setAccountType(AccountType.ADMIN);
            admin.setRole(Role.ROLE_ADMIN);
            admin.setAddress("Secretariat Main Road");
            admin.setCity("Chennai");
            admin.setState("Tamil Nadu");
            admin.setPincode("600009");
            admin.setVerificationStatus(OrgVerificationStatus.VERIFIED);
            userRepository.save(admin);
        }

        if (!demoDataEnabled) {
            return;
        }

        // Check if demo users already initialized
        if (userRepository.existsByEmail("saravana@demorestaurant.com")) {
            return;
        }

        // 1. Restaurant / Business Donor
        User saravana = new User();
        saravana.setName("Karthik Raman");
        saravana.setEmail("saravana@demorestaurant.com");
        saravana.setPassword(passwordEncoder.encode("Donor@123"));
        saravana.setPhone("+91 98402 33445");
        saravana.setAccountType(AccountType.BUSINESS_DONOR);
        saravana.setRole(Role.ROLE_BUSINESS_DONOR);
        saravana.setOrganizationName("Saravana Grand Restaurant (Demo)");
        saravana.setOrganizationType(OrgType.COMMUNITY_KITCHEN);
        saravana.setAddress("45 Usman Road, T. Nagar");
        saravana.setCity("Chennai");
        saravana.setState("Tamil Nadu");
        saravana.setPincode("600017");
        saravana.setVerificationStatus(OrgVerificationStatus.VERIFIED);
        userRepository.save(saravana);

        // 2. Another Business Donor
        User annapoorna = new User();
        annapoorna.setName("Meenakshi Iyer");
        annapoorna.setEmail("annapoorna@demorestaurant.com");
        annapoorna.setPassword(passwordEncoder.encode("Donor@123"));
        annapoorna.setPhone("+91 98403 44556");
        annapoorna.setAccountType(AccountType.BUSINESS_DONOR);
        annapoorna.setRole(Role.ROLE_BUSINESS_DONOR);
        annapoorna.setOrganizationName("Annapoorna Delights (Demo)");
        annapoorna.setOrganizationType(OrgType.COMMUNITY_KITCHEN);
        annapoorna.setAddress("12 North Mada Street, Mylapore");
        annapoorna.setCity("Chennai");
        annapoorna.setState("Tamil Nadu");
        annapoorna.setPincode("600004");
        annapoorna.setVerificationStatus(OrgVerificationStatus.VERIFIED);
        userRepository.save(annapoorna);

        // 3. Individual Donor
        User rajesh = new User();
        rajesh.setName("Rajesh Kumar");
        rajesh.setEmail("rajesh@demodonor.com");
        rajesh.setPassword(passwordEncoder.encode("Donor@123"));
        rajesh.setPhone("+91 98404 55667");
        rajesh.setAccountType(AccountType.INDIVIDUAL_DONOR);
        rajesh.setRole(Role.ROLE_INDIVIDUAL_DONOR);
        rajesh.setAddress("18 2nd Avenue, Anna Nagar");
        rajesh.setCity("Chennai");
        rajesh.setState("Tamil Nadu");
        rajesh.setPincode("600040");
        rajesh.setVerificationStatus(OrgVerificationStatus.VERIFIED);
        userRepository.save(rajesh);

        // 4. Another Individual Donor
        User priya = new User();
        priya.setName("Priya Sundaram");
        priya.setEmail("priya@demodonor.com");
        priya.setPassword(passwordEncoder.encode("Donor@123"));
        priya.setPhone("+91 98405 66778");
        priya.setAccountType(AccountType.INDIVIDUAL_DONOR);
        priya.setRole(Role.ROLE_INDIVIDUAL_DONOR);
        priya.setAddress("5 Gandhi Road, Adyar");
        priya.setCity("Chennai");
        priya.setState("Tamil Nadu");
        priya.setPincode("600020");
        priya.setVerificationStatus(OrgVerificationStatus.VERIFIED);
        userRepository.save(priya);

        // 5. Recipient Org (Verified)
        User karunai = new User();
        karunai.setName("Dr. S. Mohan");
        karunai.setEmail("karunai@demongo.org");
        karunai.setPassword(passwordEncoder.encode("Org@123"));
        karunai.setPhone("+91 98406 77889");
        karunai.setAccountType(AccountType.RECIPIENT_ORG);
        karunai.setRole(Role.ROLE_RECIPIENT_ORG);
        karunai.setOrganizationName("Karunai Care Foundation (Demo NGO)");
        karunai.setOrganizationType(OrgType.NGO);
        karunai.setAddress("77 100ft Bypass Road, Velachery");
        karunai.setCity("Chennai");
        karunai.setState("Tamil Nadu");
        karunai.setPincode("600042");
        karunai.setVerificationStatus(OrgVerificationStatus.VERIFIED);
        karunai.setVerifiedAt(LocalDateTime.now().minusDays(10));
        userRepository.save(karunai);

        // 6. Recipient Org (Verified Orphanage)
        User hope = new User();
        hope.setName("Sister Mary Joseph");
        hope.setEmail("hope@demoorphanage.org");
        hope.setPassword(passwordEncoder.encode("Org@123"));
        hope.setPhone("+91 98407 88990");
        hope.setAccountType(AccountType.RECIPIENT_ORG);
        hope.setRole(Role.ROLE_RECIPIENT_ORG);
        hope.setOrganizationName("Hope Child Haven (Demo Orphanage)");
        hope.setOrganizationType(OrgType.ORPHANAGE);
        hope.setAddress("24 GST Road, Tambaram");
        hope.setCity("Chennai");
        hope.setState("Tamil Nadu");
        hope.setPincode("600045");
        hope.setVerificationStatus(OrgVerificationStatus.VERIFIED);
        hope.setVerifiedAt(LocalDateTime.now().minusDays(15));
        userRepository.save(hope);

        // 7. Recipient Org (Pending Verification for Admin demo)
        User anbu = new User();
        anbu.setName("V. Subhashini");
        anbu.setEmail("anbu@demoelderly.org");
        anbu.setPassword(passwordEncoder.encode("Org@123"));
        anbu.setPhone("+91 98408 99001");
        anbu.setAccountType(AccountType.RECIPIENT_ORG);
        anbu.setRole(Role.ROLE_RECIPIENT_ORG);
        anbu.setOrganizationName("Anbu Elder Home (Demo Shelter)");
        anbu.setOrganizationType(OrgType.OLD_AGE_HOME);
        anbu.setAddress("88 Mount Road, Guindy");
        anbu.setCity("Chennai");
        anbu.setState("Tamil Nadu");
        anbu.setPincode("600032");
        anbu.setVerificationStatus(OrgVerificationStatus.PENDING);
        userRepository.save(anbu);

        // Seed Sample Food Donations
        FoodDonation f1 = new FoodDonation();
        f1.setDonor(saravana);
        f1.setFoodName("Fresh Vegetable Dum Biryani with Raitha");
        f1.setCategory(FoodCategory.COOKED_MEALS);
        f1.setDescription("Surplus banquet preparation. High-quality Basmati rice cooked with fresh seasonal vegetables, mint, and spices. Cleanly packed in large insulated food trays.");
        f1.setDietaryType(DietaryType.VEGETARIAN);
        f1.setQuantity(60.0);
        f1.setRemainingQuantity(60.0);
        f1.setUnit("meals");
        f1.setPreparationDateTime(LocalDateTime.now().minusHours(2));
        f1.setSafeCollectionDeadline(LocalDateTime.now().plusHours(8));
        f1.setStorageInstructions("Keep insulated or reheat to 70°C before serving");
        f1.setAllergenInfo("Contains dairy (ghee and curd)");
        f1.setPickupAddress("Saravana Grand Banquet Hall, 45 Usman Road, T. Nagar");
        f1.setCity("Chennai");
        f1.setPincode("600017");
        f1.setPickupInstructions("Enter through Kitchen Gate B. Ask for Chef Anand.");
        f1.setStatus(DonationStatus.AVAILABLE);
        foodDonationRepository.save(f1);

        FoodDonation f2 = new FoodDonation();
        f2.setDonor(annapoorna);
        f2.setFoodName("Traditional South Indian Meals (Sambar Rice & Poriyal)");
        f2.setCategory(FoodCategory.COOKED_MEALS);
        f2.setDescription("Freshly cooked pure vegetarian lunch boxes: Steamed rice, traditional drumstick sambar, mixed vegetable kootu, and chapatis.");
        f2.setDietaryType(DietaryType.VEGETARIAN);
        f2.setQuantity(40.0);
        f2.setRemainingQuantity(40.0);
        f2.setUnit("meals");
        f2.setPreparationDateTime(LocalDateTime.now().minusHours(1));
        f2.setSafeCollectionDeadline(LocalDateTime.now().plusHours(6));
        f2.setStorageInstructions("Store in warm containers");
        f2.setAllergenInfo("Contains mustard seeds, asafoetida");
        f2.setPickupAddress("Annapoorna Delights, 12 North Mada Street, Mylapore");
        f2.setCity("Chennai");
        f2.setPincode("600004");
        f2.setPickupInstructions("Collect from parcel counter with food-grade transport bags.");
        f2.setStatus(DonationStatus.AVAILABLE);
        foodDonationRepository.save(f2);

        FoodDonation f3 = new FoodDonation();
        f3.setDonor(rajesh);
        f3.setFoodName("Whole Wheat Bread & Sandwich Buns");
        f3.setCategory(FoodCategory.BAKERY_BREAD);
        f3.setDescription("Freshly packaged artisan whole wheat sandwich loaves and milk buns from morning event.");
        f3.setDietaryType(DietaryType.VEGETARIAN);
        f3.setQuantity(30.0);
        f3.setRemainingQuantity(30.0);
        f3.setUnit("packets");
        f3.setPreparationDateTime(LocalDateTime.now().minusHours(4));
        f3.setSafeCollectionDeadline(LocalDateTime.now().plusHours(36));
        f3.setStorageInstructions("Cool, dry room temperature");
        f3.setAllergenInfo("Contains wheat gluten");
        f3.setPickupAddress("18 2nd Avenue, Anna Nagar");
        f3.setCity("Chennai");
        f3.setPincode("600040");
        f3.setPickupInstructions("Call ahead 15 minutes before arrival.");
        f3.setStatus(DonationStatus.AVAILABLE);
        foodDonationRepository.save(f3);

        FoodDonation f4 = new FoodDonation();
        f4.setDonor(saravana);
        f4.setFoodName("Fresh Farm Apples, Oranges & Bananas");
        f4.setCategory(FoodCategory.FRUITS_VEGETABLES);
        f4.setDescription("Crates of fresh seasonal fruits in prime condition suitable for school children and shelter homes.");
        f4.setDietaryType(DietaryType.VEGAN);
        f4.setQuantity(45.0);
        f4.setRemainingQuantity(45.0);
        f4.setUnit("kg");
        f4.setPreparationDateTime(LocalDateTime.now().minusHours(5));
        f4.setSafeCollectionDeadline(LocalDateTime.now().plusHours(48));
        f4.setStorageInstructions("Well-ventilated storage");
        f4.setAllergenInfo("None");
        f4.setPickupAddress("45 Usman Road, T. Nagar");
        f4.setCity("Chennai");
        f4.setPincode("600017");
        f4.setPickupInstructions("Loading dock open from 10 AM to 6 PM.");
        f4.setStatus(DonationStatus.AVAILABLE);
        foodDonationRepository.save(f4);

        // Seed Sample Material Donations
        MaterialDonation m1 = new MaterialDonation();
        m1.setDonor(priya);
        m1.setItemName("Warm Winter Blankets & Cotton Bedding");
        m1.setCategory(MaterialCategory.BLANKETS_BEDDING);
        m1.setDescription("Clean, laundered thick fleece blankets and double bedsheets suitable for night shelters and orphanages.");
        m1.setQuantity(50);
        m1.setRemainingQuantity(50);
        m1.setUnit("sets");
        m1.setConditionStatus(ItemCondition.LIKE_NEW);
        m1.setPickupAddress("5 Gandhi Road, Adyar");
        m1.setCity("Chennai");
        m1.setPincode("600020");
        m1.setPreferredPickupDate(LocalDate.now().plusDays(2));
        m1.setPickupInstructions("Packaged in waterproof plastic bags ready for loading.");
        m1.setStatus(DonationStatus.AVAILABLE);
        materialDonationRepository.save(m1);

        MaterialDonation m2 = new MaterialDonation();
        m2.setDonor(rajesh);
        m2.setItemName("CBSE Standard 8-10 Science & Math Books");
        m2.setCategory(MaterialCategory.BOOKS_STATIONERY);
        m2.setDescription("Complete sets of reference textbooks, notebooks, geometric instruments, and reading storybooks in English and Tamil.");
        m2.setQuantity(80);
        m2.setRemainingQuantity(80);
        m2.setUnit("items");
        m2.setConditionStatus(ItemCondition.GOOD);
        m2.setPickupAddress("18 2nd Avenue, Anna Nagar");
        m2.setCity("Chennai");
        m2.setPincode("600040");
        m2.setPreferredPickupDate(LocalDate.now().plusDays(1));
        m2.setPickupInstructions("Packed in 3 sturdy cardboard cartons.");
        m2.setStatus(DonationStatus.AVAILABLE);
        materialDonationRepository.save(m2);

        MaterialDonation m3 = new MaterialDonation();
        m3.setDonor(priya);
        m3.setItemName("Children's Warm Clothing & Sweaters (Ages 4-12)");
        m3.setCategory(MaterialCategory.CLOTHES_FOOTWEAR);
        m3.setDescription("Sorted by age groups, washed and sanitized hoodies, sweaters, and trousers for young children.");
        m3.setQuantity(40);
        m3.setRemainingQuantity(40);
        m3.setUnit("items");
        m3.setConditionStatus(ItemCondition.LIKE_NEW);
        m3.setPickupAddress("5 Gandhi Road, Adyar");
        m3.setCity("Chennai");
        m3.setPincode("600020");
        m3.setPreferredPickupDate(LocalDate.now().plusDays(3));
        m3.setPickupInstructions("Please carry bags for easy transfer.");
        m3.setStatus(DonationStatus.AVAILABLE);
        materialDonationRepository.save(m3);

        MaterialDonation m4 = new MaterialDonation();
        m4.setDonor(rajesh);
        m4.setItemName("Dry Rations: Rice, Toor Dal & Cooking Oil");
        m4.setCategory(MaterialCategory.GROCERIES_RATIONS);
        m4.setDescription("Sealed brand-new 10kg bags of Sona Masoori rice, 5kg bags of Toor Dal, and 5L sunflower oil cans.");
        m4.setQuantity(25);
        m4.setRemainingQuantity(25);
        m4.setUnit("sets");
        m4.setConditionStatus(ItemCondition.NEW);
        m4.setExpiryDate(LocalDate.now().plusMonths(6));
        m4.setPickupAddress("18 2nd Avenue, Anna Nagar");
        m4.setCity("Chennai");
        m4.setPincode("600040");
        m4.setPreferredPickupDate(LocalDate.now().plusDays(2));
        m4.setPickupInstructions("Heavy luggage, small auto or van recommended.");
        m4.setStatus(DonationStatus.AVAILABLE);
        materialDonationRepository.save(m4);

        // Seed Sample Cash Campaigns
        CashCampaign c1 = new CashCampaign();
        c1.setOrganization(hope);
        c1.setTitle("Nutritious Hot Meals for 100 Orphanage Children");
        c1.setCauseCategory(CauseCategory.CHILD_WELFARE);
        c1.setDescription("Providing healthy breakfast and balanced dinner for 100 resident children at Hope Child Haven Orphanage, Tambaram. Funds cover milk, fruits, protein-rich pulses, and kitchen cooking fuel.");
        c1.setTargetAmount(BigDecimal.valueOf(75000.00));
        c1.setRaisedAmount(BigDecimal.valueOf(42500.00));
        c1.setDeadline(LocalDate.now().plusDays(30));
        c1.setApprovalStatus(ApprovalStatus.APPROVED);
        c1.setStatus(CampaignStatus.ACTIVE);
        cashCampaignRepository.save(c1);

        CashCampaign c2 = new CashCampaign();
        c2.setOrganization(karunai);
        c2.setTitle("Chennai Winter Warmth & Shelter Relief Drive");
        c2.setCauseCategory(CauseCategory.EMERGENCY_RELIEF);
        c2.setDescription("Distributing thermal blankets, floor sleeping mats, and warm night clothing to homeless pavement dwellers and shelter inmates across Central and South Chennai.");
        c2.setTargetAmount(BigDecimal.valueOf(50000.00));
        c2.setRaisedAmount(BigDecimal.valueOf(28000.00));
        c2.setDeadline(LocalDate.now().plusDays(20));
        c2.setApprovalStatus(ApprovalStatus.APPROVED);
        c2.setStatus(CampaignStatus.ACTIVE);
        cashCampaignRepository.save(c2);

        CashCampaign c3 = new CashCampaign();
        c3.setOrganization(hope);
        c3.setTitle("School Kit & Educational Support for Slum Children");
        c3.setCauseCategory(CauseCategory.EDUCATION);
        c3.setDescription("Equipping 200 underprivileged students with school bags, notebooks, geometry boxes, uniforms, and shoes for the upcoming school term.");
        c3.setTargetAmount(BigDecimal.valueOf(40000.00));
        c3.setRaisedAmount(BigDecimal.valueOf(16500.00));
        c3.setDeadline(LocalDate.now().plusDays(45));
        c3.setApprovalStatus(ApprovalStatus.APPROVED);
        c3.setStatus(CampaignStatus.ACTIVE);
        cashCampaignRepository.save(c3);

        CashCampaign c4 = new CashCampaign();
        c4.setOrganization(karunai);
        c4.setTitle("Emergency Monsoon Relief Food & Grocery Kits");
        c4.setCauseCategory(CauseCategory.EMERGENCY_RELIEF);
        c4.setDescription("Emergency prepared food ration packets for waterlogged areas in North Chennai.");
        c4.setTargetAmount(BigDecimal.valueOf(90000.00));
        c4.setRaisedAmount(BigDecimal.ZERO);
        c4.setDeadline(LocalDate.now().plusDays(15));
        c4.setApprovalStatus(ApprovalStatus.PENDING); // For Admin demo approval!
        c4.setStatus(CampaignStatus.ACTIVE);
        cashCampaignRepository.save(c4);

        // Seed Sample Demo Transactions
        CashTransaction t1 = new CashTransaction();
        t1.setCampaign(c1);
        t1.setDonor(rajesh);
        t1.setDonorName("Rajesh Kumar");
        t1.setDonorEmail("rajesh@demodonor.com");
        t1.setAmount(BigDecimal.valueOf(2500.00));
        t1.setPaymentMode("DEMO_SIMULATION");
        t1.setTransactionReference("FB-DEMO-TXN-1001");
        t1.setDonorMessage("May every child enjoy wholesome and nutritious meals!");
        t1.setConfirmed(true);
        cashTransactionRepository.save(t1);

        CashTransaction t2 = new CashTransaction();
        t2.setCampaign(c1);
        t2.setDonor(priya);
        t2.setDonorName("Priya Sundaram");
        t2.setDonorEmail("priya@demodonor.com");
        t2.setAmount(BigDecimal.valueOf(1000.00));
        t2.setPaymentMode("DEMO_SIMULATION");
        t2.setTransactionReference("FB-DEMO-TXN-1002");
        t2.setDonorMessage("Supporting this wonderful initiative.");
        t2.setConfirmed(true);
        cashTransactionRepository.save(t2);

        // Seed Sample Community Needs
        CommunityNeed n1 = new CommunityNeed();
        n1.setOrganization(hope);
        n1.setTitle("Daily Hot Lunch for 90 Resident Orphanage Children");
        n1.setCategory(NeedCategory.FOOD);
        n1.setDescription("Urgent requirement for daily prepared lunch or dry ration sponsorship for children aged 5-16 at Tambaram Orphanage.");
        n1.setTargetQuantity(90.0);
        n1.setFulfilledQuantity(35.0);
        n1.setUnit("meals");
        n1.setPriority(NeedPriority.URGENT);
        n1.setTargetDate(LocalDate.now().plusDays(7));
        n1.setCity("Chennai");
        n1.setPincode("600045");
        n1.setStatus(NeedStatus.OPEN);
        communityNeedRepository.save(n1);

        CommunityNeed n2 = new CommunityNeed();
        n2.setOrganization(karunai);
        n2.setTitle("80 Thick Winter Blankets for Night Shelters");
        n2.setCategory(NeedCategory.BLANKETS);
        n2.setDescription("Immediate requirement of clean blankets for elderly homeless individuals accommodated at Velachery night shelter.");
        n2.setTargetQuantity(80.0);
        n2.setFulfilledQuantity(45.0);
        n2.setUnit("blankets");
        n2.setPriority(NeedPriority.HIGH);
        n2.setTargetDate(LocalDate.now().plusDays(10));
        n2.setCity("Chennai");
        n2.setPincode("600042");
        n2.setStatus(NeedStatus.OPEN);
        communityNeedRepository.save(n2);

        CommunityNeed n3 = new CommunityNeed();
        n3.setOrganization(hope);
        n3.setTitle("50 School Bags & Stationery Sets for Class 1 to 5");
        n3.setCategory(NeedCategory.SCHOOL_SUPPLIES);
        n3.setDescription("Looking for donors to contribute water bottles, pencils, notebooks, and sturdy school bags.");
        n3.setTargetQuantity(50.0);
        n3.setFulfilledQuantity(20.0);
        n3.setUnit("kits");
        n3.setPriority(NeedPriority.MEDIUM);
        n3.setTargetDate(LocalDate.now().plusDays(25));
        n3.setCity("Chennai");
        n3.setPincode("600045");
        n3.setStatus(NeedStatus.OPEN);
        communityNeedRepository.save(n3);

        CommunityNeed n4 = new CommunityNeed();
        n4.setOrganization(anbu);
        n4.setTitle("Adult Diapers & Sanitization Hygiene Supplies");
        n4.setCategory(NeedCategory.HYGIENE_KITS);
        n4.setDescription("Monthly hygiene supplies and antiseptics for 40 bedridden elderly citizens.");
        n4.setTargetQuantity(40.0);
        n4.setFulfilledQuantity(10.0);
        n4.setUnit("packs");
        n4.setPriority(NeedPriority.HIGH);
        n4.setTargetDate(LocalDate.now().plusDays(14));
        n4.setCity("Chennai");
        n4.setPincode("600032");
        n4.setStatus(NeedStatus.OPEN);
        communityNeedRepository.save(n4);
    }
}

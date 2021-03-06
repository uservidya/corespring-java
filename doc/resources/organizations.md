## Organizations

A CoreSpring [Organization](/src/main/java/org/corespring/resource/Organization.java) contains information about an
organization within the CoreSpring platform. Organizations are typically development partners who work with CoreSpring,
and **not** groups within an educational institution (districts, schools, etc.). After registering for the CoreSpring
platform, a developer will have a single Organization associated with their account. You should be able to retrieve
your current organization from [CorespringClient](/src/main/java/org/corespring/CorespringClient.java)'s
getOrganizations method after you initialize it with your access token.


### CorespringClient methods

#### List organizations

    CorespringClient client = new CorespringClient(clientId, clientSecret);

    Collection<Organization> organizations = client.getOrganizations();
    for (Organization organization : organizations) {
      System.out.println(organization.getName());       // "Demo Organization"
      System.out.println(organization.getId());         // "51114b307fc1eaa866444648"
    }
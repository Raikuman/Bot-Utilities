# Bot-Utilities
A JDA5 bot utility library for making it easier to code my bots.

Check out the Trello [here](https://trello.com/b/1M1zxBSj/bot-utilities)

---

## Commands
Create a command by implementing `CommandInterface`:
```java
public class ExampleCommand implements CommandInterface {
	
	@Override
	public void handle(CommandContext ctx) {
		// Command functionality
	}
	
	// How to invoke the command
	@Override
	public String getInvoke() {
		return "example";
	}

	// How the command should be used
	@Override
	public String getUsage() {
		return "";
	}
	
	// A description of what the command does
	@Override
	public String getDescription() {
		return "An example command";
	}
}
```

After creating your command/commands, add it to a `ListenerBuilder` and build the listener manager:
```java

List<CommandInterface> commands = Arrays.asList(
	new ExampleCommand(),
	new OtherCommand()
);

ListenerManager listenerManager = new ListenerBuilder()
	.setCommands(commands)
	.build();

```

The listener manager can then be used to provide listeners to your JDA object:
```java
JDA jda = JDABuilder
	...
	...
	.addEventListeners(listenerManager.getListeners())
	.build();
```

---

## Pagination
The interface `PageInvokeInterface` provides methods to override for pagination settings. Paginations 
can be implemented along with`CommandInterface` or `SelectInterface`.

A `Pagination` class exists to help provide buttons and embeds:
```java
Pagination(
	Member member, 
	String invoke, 
	List<String> paginationStrings, 
	int itemsPerPage,
	boolean loop
)
```

Provide `ItemComponent` buttons using the `Pagination` object:
```java
Pagination pagination = new Pagination(
	member,
	invoke,
	paginationStrings,
	3,
	true
);

List<ItemComponent> componentList = Arrays.asList(
	pagination.provideLeft(),
	pagination.provideFirst(),
	pagination.provideRight()
);
```

The `Pagination` object also has a `buildEmbeds()` method to provide embeds for pagination. 
Pagination can be implemented as such:

```java
public class ExampleCommand implements CommandInterface, PageInvokeInterface {
	
	@Override
	public void handle(CommandContext ctx) {
		Pagination pagination = new Pagination(
			ctx.getEventMember(),
			getInvoke(),
			pageStrings(ctx),
			itemsPerPage(),
			loopPagination()
		);
	
		List<ItemComponent> componentList = Arrays.asList(
			pagination.provideLeft(),
			pagination.provideFirst(),
			pagination.provideRight()
		);
	
		ctx.getChannel().sendMessageEmbeds(
			pagination.buildEmbeds().get(0).build()
		).setActionRow(componentList).queue();
	
		ctx.getEvent().getMessage().delete().queue();
	}
	
	// How to invoke the command
	@Override
	public String getInvoke() {
		return "example";
	}

	// How the command should be used (if there are any arguments)
	@Override
	public String getUsage() {
		return "";
	}
	
	// A description of what the command does
	@Override
	public String getDescription() {
		return "An example command";
	}
	
	/*
		Below are the functions to override when implementing PageInvokeInterface
	 */
	
	// A list of strings to write to pages in pagination
	@Override
	public List<String> pageStrings(EventContext ctx) {
		return Arrays.asList(
			"Hello!",
			"This is an entry",
			"In a",
			"Pagination!"
		);
	}

	// The number of strings from pageStrings to write on a single page
	@Override
	public int itemsPerPage() {
		return 3;
	}
	
	// Whether to loop around a pagination when reaching the end
	@Override
	public boolean loopPagination() {
		return true;
	}
	
	// Whether a button to go back to the previous home embed should be added
	// For example, having a select menu going into paginations, the home button would
	// return to the select menu
	@Override
	public boolean addHomeBtn() {
		return false;
	}
	
	// Whether a button to go back to the first page of a pagination should be added
	@Override
	public boolean addFirstPageBtn() {
		return true;
	}
}
```

Pagination buttons need to be added to the `ListenerManager`. Using the `PaginationButtonProvider` will 
make providing `ButtonInterface` buttons from pagination commands easier:
```java
List<CommandInterface> commands = Arrays.asList(
	new ExampleCommand(),
	new OtherCommand()
);

List<ButtonInterface> buttons = new ArrayList<>();
buttons.addAll(PaginationButtonProvider.provideButtons(new ExampleCommand()));

ListenerManager listenerManager = new ListenerBuilder()
	.setCommands(commands)
	.setButtons(buttons)
	.build();
```

---

## Select Menus
Commands (and even select menus) do not implement `SelectInterface`. 

Another class should implement `SelectInterface`:
```java
public class MenuOne implements SelectInterface {

	@Override
	public void handle(SelectContext ctx) {
		// Select functionality
	}
	
	// The value of the select menu when selected
	@Override
	public String getMenuValue() {
		return "menu1";
	}

	// What is shown on the select menu
	@Override
	public String getLabel() {
		return "Menu 1";
	}
}
```

The command needs to add an `ActionRow` of a `SelectMenu` to the message so that your `SelectInterface` 
classes will work:
```java
@Override
public void handle(CommandContext ctx) {
	MenuOne menuOne = new MenuOne();
	
	SelectMenu selectMenu = SelectMenu.create(ctx.getEventMember().getId() + ":" + getInvoke())
		.setPlaceholder("Placeholder text")
		.setRequiredRange(1, 1)
		.addOption(menuOne.getLabel() , memberId + ":" + menuOne.getMenuValue())
		.build();
	
	ctx.getChannel().sendMessageEmbeds(
		// build embed here
	).setActionRows(ActionRow.of(selectMenu)).queue();
}
```

`SelectInterfaces` need to be added to the `ListenerManager`:
```java
List<CommandInterface> commands = Arrays.asList(
	new ExampleCommand(),
	new OtherCommand()
);

List<SelectInterface> selects = new ArrayList<>();
selectInterfaces.add(new MenuOne());
	
ListenerManager listenerManager = new ListenerBuilder()
	.setCommands(commands)
	.setSelects(selects)
	.build();
```


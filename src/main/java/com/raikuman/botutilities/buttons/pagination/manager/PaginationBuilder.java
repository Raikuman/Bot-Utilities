package com.raikuman.botutilities.buttons.pagination.manager;

import com.raikuman.botutilities.buttons.pagination.PaginationResources;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;

/**
 * A pagination builder to provide pagination and embeds
 *
 * @version 1.0 2022-19-06
 * @since 1.0
 */
public class PaginationBuilder {

	private final Member member;
	private final String invoke;
	private final List<String> paginationStrings;
	private final int itemsPerPage;
	private final boolean loop;

	public PaginationBuilder(Member member, String invoke, List<String> paginationStrings, int itemsPerPage,
		boolean loop) {
		this.member = member;
		this.invoke = invoke;
		this.paginationStrings = paginationStrings;
		this.itemsPerPage = itemsPerPage;
		this.loop = loop;
	}

	/**
	 * Returns a Pagination object to get buttons from
	 * @return The Pagination object
	 */
	public Pagination build() {
		return new Pagination(invoke, member.getId(), loop);
	}

	/**
	 * Returns an EmbedBuilder list to provide embeds
	 * @return The list of EmbedBuilders
	 */
	public List<EmbedBuilder> buildEmbeds() {
		return PaginationResources.buildEmbeds(
			invoke,
			member.getEffectiveAvatarUrl(),
			paginationStrings,
			itemsPerPage
		);
	}
}

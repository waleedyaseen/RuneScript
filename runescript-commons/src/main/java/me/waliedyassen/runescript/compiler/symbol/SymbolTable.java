/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.symbol.impl.ConstantInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.RuntimeConstantInfo;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a compile-time symbol table, it contains various information about different symbol types such as
 * constants, commands, scripts, and global variables.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public class SymbolTable {

    /**
     * The defined constants map.
     */
    @Getter
    private final SymbolList<ConstantInfo> constants = new SymbolList<>();

    /**
     * The defined configurations map.
     */
    @Getter
    private final Map<Type, SymbolList<ConfigSymbol>> configs = new HashMap<>();

    /**
     * The defined runtime constants.
     */
    @Getter
    private final SymbolList<RuntimeConstantInfo> runtimeConstants = new SymbolList<>();

    /**
     * The parent symbol table.
     */
    @Getter
    private final SymbolTable parent;

    /**
     * Whether to allow the un-defining of symbols from this table.
     */
    @Getter
    protected final boolean allowRemoving;

    /**
     * Constructs a new {@link SymbolTable} type object instance.
     *
     * @param allowRemoving whether to allow removing symbols from this table.
     */
    public SymbolTable(boolean allowRemoving) {
        this(null, allowRemoving);
        int count = 0;
        constants.add(new ConstantInfo("false", count++, PrimitiveType.INT.INSTANCE, 0));
        constants.add(new ConstantInfo("true", count++, PrimitiveType.INT.INSTANCE, 1));
        constants.add(new ConstantInfo("iftype_layer", count++, PrimitiveType.INT.INSTANCE, 0));
        constants.add(new ConstantInfo("iftype_inventory", count++, PrimitiveType.INT.INSTANCE, 2));
        constants.add(new ConstantInfo("iftype_rectangle", count++, PrimitiveType.INT.INSTANCE, 3));
        constants.add(new ConstantInfo("iftype_text", count++, PrimitiveType.INT.INSTANCE, 4));
        constants.add(new ConstantInfo("iftype_graphic", count++, PrimitiveType.INT.INSTANCE, 5));
        constants.add(new ConstantInfo("iftype_model", count++, PrimitiveType.INT.INSTANCE, 6));
        constants.add(new ConstantInfo("iftype_line", count++, PrimitiveType.INT.INSTANCE, 9));
        constants.add(new ConstantInfo("setsize_abs", count++, PrimitiveType.INT.INSTANCE, 0));
        constants.add(new ConstantInfo("setsize_minus", count++, PrimitiveType.INT.INSTANCE, 1));
        constants.add(new ConstantInfo("setsize_proportion", count++, PrimitiveType.INT.INSTANCE, 2));
        constants.add(new ConstantInfo("setsize_3", count++, PrimitiveType.INT.INSTANCE, 3));
        constants.add(new ConstantInfo("setsize_aspect", count++, PrimitiveType.INT.INSTANCE, 4));
        constants.add(new ConstantInfo("setpos_abs_left", count++, PrimitiveType.INT.INSTANCE, 0));
        constants.add(new ConstantInfo("setpos_abs_centre", count++, PrimitiveType.INT.INSTANCE, 1));
        constants.add(new ConstantInfo("setpos_abs_right", count++, PrimitiveType.INT.INSTANCE, 2));
        constants.add(new ConstantInfo("setpos_proportion_left", count++, PrimitiveType.INT.INSTANCE, 3));
        constants.add(new ConstantInfo("setpos_proportion_centre", count++, PrimitiveType.INT.INSTANCE, 4));
        constants.add(new ConstantInfo("setpos_proportion_right", count++, PrimitiveType.INT.INSTANCE, 5));
        constants.add(new ConstantInfo("setpos_abs_top", count++, PrimitiveType.INT.INSTANCE, 0));
        constants.add(new ConstantInfo("setpos_abs_bottom", count++, PrimitiveType.INT.INSTANCE, 2));
        constants.add(new ConstantInfo("setpos_proportion_top", count++, PrimitiveType.INT.INSTANCE, 3));
        constants.add(new ConstantInfo("setpos_proportion_bottom", count++, PrimitiveType.INT.INSTANCE, 5));
        constants.add(new ConstantInfo("settextalign_left", count++, PrimitiveType.INT.INSTANCE, 0));
        constants.add(new ConstantInfo("settextalign_centre", count++, PrimitiveType.INT.INSTANCE, 1));
        constants.add(new ConstantInfo("settextalign_right", count++, PrimitiveType.INT.INSTANCE, 2));
        constants.add(new ConstantInfo("settextalign_justify", count++, PrimitiveType.INT.INSTANCE, 3));
        constants.add(new ConstantInfo("settextalign_top", count++, PrimitiveType.INT.INSTANCE, 0));
        constants.add(new ConstantInfo("settextalign_bottom", count++, PrimitiveType.INT.INSTANCE, 2));
        constants.add(new ConstantInfo("min_32bit_int", count++, PrimitiveType.INT.INSTANCE, Integer.MIN_VALUE));
        constants.add(new ConstantInfo("max_32bit.int", count++, PrimitiveType.INT.INSTANCE, Integer.MAX_VALUE));
        constants.add(new ConstantInfo("key_f1", count++, PrimitiveType.INT.INSTANCE, 1));
        constants.add(new ConstantInfo("key_f2", count++, PrimitiveType.INT.INSTANCE, 2));
        constants.add(new ConstantInfo("key_f3", count++, PrimitiveType.INT.INSTANCE, 3));
        constants.add(new ConstantInfo("key_f4", count++, PrimitiveType.INT.INSTANCE, 4));
        constants.add(new ConstantInfo("key_f5", count++, PrimitiveType.INT.INSTANCE, 5));
        constants.add(new ConstantInfo("key_f6", count++, PrimitiveType.INT.INSTANCE, 6));
        constants.add(new ConstantInfo("key_f7", count++, PrimitiveType.INT.INSTANCE, 7));
        constants.add(new ConstantInfo("key_f8", count++, PrimitiveType.INT.INSTANCE, 8));
        constants.add(new ConstantInfo("key_f9", count++, PrimitiveType.INT.INSTANCE, 9));
        constants.add(new ConstantInfo("key_f10", count++, PrimitiveType.INT.INSTANCE, 10));
        constants.add(new ConstantInfo("key_f11", count++, PrimitiveType.INT.INSTANCE, 11));
        constants.add(new ConstantInfo("key_f12", count++, PrimitiveType.INT.INSTANCE, 12));
        constants.add(new ConstantInfo("key_escape", count++, PrimitiveType.INT.INSTANCE, 13));
        constants.add(new ConstantInfo("key_1", count++, PrimitiveType.INT.INSTANCE, 16));
        constants.add(new ConstantInfo("key_2", count++, PrimitiveType.INT.INSTANCE, 17));
        constants.add(new ConstantInfo("key_3", count++, PrimitiveType.INT.INSTANCE, 18));
        constants.add(new ConstantInfo("key_4", count++, PrimitiveType.INT.INSTANCE, 19));
        constants.add(new ConstantInfo("key_5", count++, PrimitiveType.INT.INSTANCE, 20));
        constants.add(new ConstantInfo("key_6", count++, PrimitiveType.INT.INSTANCE, 21));
        constants.add(new ConstantInfo("key_7", count++, PrimitiveType.INT.INSTANCE, 22));
        constants.add(new ConstantInfo("key_8", count++, PrimitiveType.INT.INSTANCE, 23));
        constants.add(new ConstantInfo("key_9", count++, PrimitiveType.INT.INSTANCE, 24));
        constants.add(new ConstantInfo("key_0", count++, PrimitiveType.INT.INSTANCE, 25));
        constants.add(new ConstantInfo("key_minus", count++, PrimitiveType.INT.INSTANCE, 26));
        constants.add(new ConstantInfo("key_equals", count++, PrimitiveType.INT.INSTANCE, 27));
        constants.add(new ConstantInfo("key_console", count++, PrimitiveType.INT.INSTANCE, 28));
        constants.add(new ConstantInfo("key_q", count++, PrimitiveType.INT.INSTANCE, 32));
        constants.add(new ConstantInfo("key_w", count++, PrimitiveType.INT.INSTANCE, 33));
        constants.add(new ConstantInfo("key_e", count++, PrimitiveType.INT.INSTANCE, 34));
        constants.add(new ConstantInfo("key_r", count++, PrimitiveType.INT.INSTANCE, 35));
        constants.add(new ConstantInfo("key_t", count++, PrimitiveType.INT.INSTANCE, 36));
        constants.add(new ConstantInfo("key_y", count++, PrimitiveType.INT.INSTANCE, 37));
        constants.add(new ConstantInfo("key_u", count++, PrimitiveType.INT.INSTANCE, 38));
        constants.add(new ConstantInfo("key_i", count++, PrimitiveType.INT.INSTANCE, 39));
        constants.add(new ConstantInfo("key_o", count++, PrimitiveType.INT.INSTANCE, 40));
        constants.add(new ConstantInfo("key_p", count++, PrimitiveType.INT.INSTANCE, 41));
        constants.add(new ConstantInfo("key_left_bracket", count++, PrimitiveType.INT.INSTANCE, 42));
        constants.add(new ConstantInfo("key_right_bracket", count++, PrimitiveType.INT.INSTANCE, 43));
        constants.add(new ConstantInfo("key_a", count++, PrimitiveType.INT.INSTANCE, 48));
        constants.add(new ConstantInfo("key_s", count++, PrimitiveType.INT.INSTANCE, 49));
        constants.add(new ConstantInfo("key_d", count++, PrimitiveType.INT.INSTANCE, 50));
        constants.add(new ConstantInfo("key_f", count++, PrimitiveType.INT.INSTANCE, 51));
        constants.add(new ConstantInfo("key_g", count++, PrimitiveType.INT.INSTANCE, 52));
        constants.add(new ConstantInfo("key_h", count++, PrimitiveType.INT.INSTANCE, 53));
        constants.add(new ConstantInfo("key_j", count++, PrimitiveType.INT.INSTANCE, 54));
        constants.add(new ConstantInfo("key_k", count++, PrimitiveType.INT.INSTANCE, 55));
        constants.add(new ConstantInfo("key_l", count++, PrimitiveType.INT.INSTANCE, 56));
        constants.add(new ConstantInfo("key_semicolon", count++, PrimitiveType.INT.INSTANCE, 57));
        constants.add(new ConstantInfo("key_apostrophe", count++, PrimitiveType.INT.INSTANCE, 58));
        constants.add(new ConstantInfo("key_win_left", count++, PrimitiveType.INT.INSTANCE, 59));
        constants.add(new ConstantInfo("key_z", count++, PrimitiveType.INT.INSTANCE, 64));
        constants.add(new ConstantInfo("key_x", count++, PrimitiveType.INT.INSTANCE, 65));
        constants.add(new ConstantInfo("key_c", count++, PrimitiveType.INT.INSTANCE, 66));
        constants.add(new ConstantInfo("key_v", count++, PrimitiveType.INT.INSTANCE, 67));
        constants.add(new ConstantInfo("key_b", count++, PrimitiveType.INT.INSTANCE, 68));
        constants.add(new ConstantInfo("key_n", count++, PrimitiveType.INT.INSTANCE, 69));
        constants.add(new ConstantInfo("key_m", count++, PrimitiveType.INT.INSTANCE, 70));
        constants.add(new ConstantInfo("key_comma", count++, PrimitiveType.INT.INSTANCE, 71));
        constants.add(new ConstantInfo("key_period", count++, PrimitiveType.INT.INSTANCE, 72));
        constants.add(new ConstantInfo("key_slash", count++, PrimitiveType.INT.INSTANCE, 73));
        constants.add(new ConstantInfo("key_backslash", count++, PrimitiveType.INT.INSTANCE, 74));
        constants.add(new ConstantInfo("key_tab", count++, PrimitiveType.INT.INSTANCE, 80));
        constants.add(new ConstantInfo("key_shift_left", count++, PrimitiveType.INT.INSTANCE, 81));
        constants.add(new ConstantInfo("key_control_left", count++, PrimitiveType.INT.INSTANCE, 82));
        constants.add(new ConstantInfo("key_space", count++, PrimitiveType.INT.INSTANCE, 83));
        constants.add(new ConstantInfo("key_return", count++, PrimitiveType.INT.INSTANCE, 84));
        constants.add(new ConstantInfo("key_backspace", count++, PrimitiveType.INT.INSTANCE, 85));
        constants.add(new ConstantInfo("key_alt_left", count++, PrimitiveType.INT.INSTANCE, 86));
        constants.add(new ConstantInfo("key_numpad_add", count++, PrimitiveType.INT.INSTANCE, 87));
        constants.add(new ConstantInfo("key_numpad_subtract", count++, PrimitiveType.INT.INSTANCE, 88));
        constants.add(new ConstantInfo("key_numpad_multiply", count++, PrimitiveType.INT.INSTANCE, 89));
        constants.add(new ConstantInfo("key_numpad_divide", count++, PrimitiveType.INT.INSTANCE, 90));
        constants.add(new ConstantInfo("key_clear", count++, PrimitiveType.INT.INSTANCE, 91));
        constants.add(new ConstantInfo("key_left", count++, PrimitiveType.INT.INSTANCE, 96));
        constants.add(new ConstantInfo("key_right", count++, PrimitiveType.INT.INSTANCE, 97));
        constants.add(new ConstantInfo("key_up", count++, PrimitiveType.INT.INSTANCE, 98));
        constants.add(new ConstantInfo("key_down", count++, PrimitiveType.INT.INSTANCE, 99));
        constants.add(new ConstantInfo("key_insert", count++, PrimitiveType.INT.INSTANCE, 100));
        constants.add(new ConstantInfo("key_del", count++, PrimitiveType.INT.INSTANCE, 101));
        constants.add(new ConstantInfo("key_home", count++, PrimitiveType.INT.INSTANCE, 102));
        constants.add(new ConstantInfo("key_end", count++, PrimitiveType.INT.INSTANCE, 103));
        constants.add(new ConstantInfo("key_page_up", count++, PrimitiveType.INT.INSTANCE, 104));
        constants.add(new ConstantInfo("key_page_down", count++, PrimitiveType.INT.INSTANCE, 105));
        constants.add(new ConstantInfo("red", count++, PrimitiveType.INT.INSTANCE, 0xFF0000));
        constants.add(new ConstantInfo("green", count++, PrimitiveType.INT.INSTANCE, 0x00FF00));
        constants.add(new ConstantInfo("blue", count++, PrimitiveType.INT.INSTANCE, 0x0000FF));
        constants.add(new ConstantInfo("yellow", count++, PrimitiveType.INT.INSTANCE, 0xFFFF00));
        constants.add(new ConstantInfo("magenta", count++, PrimitiveType.INT.INSTANCE, 0xFF00FF));
        constants.add(new ConstantInfo("cyan", count++, PrimitiveType.INT.INSTANCE, 0x00FFFF));
        constants.add(new ConstantInfo("white", count++, PrimitiveType.INT.INSTANCE, 0xFFFFFF));
        constants.add(new ConstantInfo("black", count++, PrimitiveType.INT.INSTANCE, 0x000000));
        constants.add(new ConstantInfo("pref_brightness", count++, PrimitiveType.INT.INSTANCE, 6));
        constants.add(new ConstantInfo("pref_volume_music", count++, PrimitiveType.INT.INSTANCE, 7));
        constants.add(new ConstantInfo("pref_volume_sounds", count++, PrimitiveType.INT.INSTANCE, 8));
        constants.add(new ConstantInfo("pref_volume_ambient", count++, PrimitiveType.INT.INSTANCE, 9));
        constants.add(new ConstantInfo("pref_always_on_top", count++, PrimitiveType.INT.INSTANCE, 12));
        constants.add(new ConstantInfo("pref_draw_distance", count++, PrimitiveType.INT.INSTANCE, 14));
        constants.add(new ConstantInfo("pref_ui_scaling_mode", count++, PrimitiveType.INT.INSTANCE, 15));
        constants.add(new ConstantInfo("setting_chatbox_scrollbar_pos", count++, PrimitiveType.INT.INSTANCE, 1));
        constants.add(new ConstantInfo("setting_resizable_transparent_side_panel", count++, PrimitiveType.INT.INSTANCE, 2));
        constants.add(new ConstantInfo("setting_hitsplat_.INT.INSTANCEing", count++, PrimitiveType.INT.INSTANCE, 5));
        constants.add(new ConstantInfo("setting_special_attack_bar_tooltip", count++, PrimitiveType.INT.INSTANCE, 6));
        constants.add(new ConstantInfo("setting_toggle_roof", count++, PrimitiveType.INT.INSTANCE, 7));
        constants.add(new ConstantInfo("setting_data_orbs", count++, PrimitiveType.INT.INSTANCE, 8));
        constants.add(new ConstantInfo("setting_wiki_lookup", count++, PrimitiveType.INT.INSTANCE, 9));
        constants.add(new ConstantInfo("setting_boss_health_overlay", count++, PrimitiveType.INT.INSTANCE, 10));
        constants.add(new ConstantInfo("setting_windowmode", count++, PrimitiveType.INT.INSTANCE, 12));
        constants.add(new ConstantInfo("setting_brightness", count++, PrimitiveType.INT.INSTANCE, 15));
        constants.add(new ConstantInfo("setting_volumemusic", count++, PrimitiveType.INT.INSTANCE, 30));
        constants.add(new ConstantInfo("setting_volumesounds", count++, PrimitiveType.INT.INSTANCE, 31));
        constants.add(new ConstantInfo("setting_volumeareasounds", count++, PrimitiveType.INT.INSTANCE, 32));
        constants.add(new ConstantInfo("setting_music_unlock_message", count++, PrimitiveType.INT.INSTANCE, 33));
        constants.add(new ConstantInfo("setting_split_private_chat", count++, PrimitiveType.INT.INSTANCE, 35));
        constants.add(new ConstantInfo("setting_hide_private_chat", count++, PrimitiveType.INT.INSTANCE, 36));
        constants.add(new ConstantInfo("setting_profanity_filter", count++, PrimitiveType.INT.INSTANCE, 37));
        constants.add(new ConstantInfo("setting_loot_drop_notification", count++, PrimitiveType.INT.INSTANCE, 38));
        constants.add(new ConstantInfo("setting_loot_drop_notification_value", count++, PrimitiveType.INT.INSTANCE, 39));
        constants.add(new ConstantInfo("setting_untradeable_loot_notification", count++, PrimitiveType.INT.INSTANCE, 40));
        constants.add(new ConstantInfo("setting_drop_item_warnings", count++, PrimitiveType.INT.INSTANCE, 42));
        constants.add(new ConstantInfo("setting_drop_item_warnings_value", count++, PrimitiveType.INT.INSTANCE, 43));
        constants.add(new ConstantInfo("setting_store_button_mobile", count++, PrimitiveType.INT.INSTANCE, 47));
        constants.add(new ConstantInfo("setting_mouse_camera", count++, PrimitiveType.INT.INSTANCE, 49));
        constants.add(new ConstantInfo("setting_function_button", count++, PrimitiveType.INT.INSTANCE, 52));
        constants.add(new ConstantInfo("setting_escape_closes_interface", count++, PrimitiveType.INT.INSTANCE, 57));
        constants.add(new ConstantInfo("setting_accept_aid", count++, PrimitiveType.INT.INSTANCE, 59));
        constants.add(new ConstantInfo("setting_warning_teleport_to_target", count++, PrimitiveType.INT.INSTANCE, 60));
        constants.add(new ConstantInfo("setting_warning_dareeyak_teleport", count++, PrimitiveType.INT.INSTANCE, 61));
        constants.add(new ConstantInfo("setting_warning_carrallangar_teleport", count++, PrimitiveType.INT.INSTANCE, 62));
        constants.add(new ConstantInfo("setting_warning_annakarl_teleport", count++, PrimitiveType.INT.INSTANCE, 63));
        constants.add(new ConstantInfo("setting_warning_ghorrock_teleport", count++, PrimitiveType.INT.INSTANCE, 64));
        constants.add(new ConstantInfo("setting_warning_alch_minimum_value", count++, PrimitiveType.INT.INSTANCE, 66));
        constants.add(new ConstantInfo("setting_warning_ice_plateau_tablet", count++, PrimitiveType.INT.INSTANCE, 67));
        constants.add(new ConstantInfo("setting_warning_cemetery_tablet", count++, PrimitiveType.INT.INSTANCE, 68));
        constants.add(new ConstantInfo("setting_warning_crabs_tablet", count++, PrimitiveType.INT.INSTANCE, 69));
        constants.add(new ConstantInfo("setting_warning_dareeyak_tablet", count++, PrimitiveType.INT.INSTANCE, 70));
        constants.add(new ConstantInfo("setting_warning_carrallangar_tablet", count++, PrimitiveType.INT.INSTANCE, 71));
        constants.add(new ConstantInfo("setting_warning_annakarl_tablet", count++, PrimitiveType.INT.INSTANCE, 72));
        constants.add(new ConstantInfo("setting_warning_ghorrock_tablet", count++, PrimitiveType.INT.INSTANCE, 73));
        constants.add(new ConstantInfo("setting_interface_scaling", count++, PrimitiveType.INT.INSTANCE, 79));
        constants.add(new ConstantInfo("setting_precise_timing", count++, PrimitiveType.INT.INSTANCE, 82));
        constants.add(new ConstantInfo("setting_separate_hours", count++, PrimitiveType.INT.INSTANCE, 83));
        constants.add(new ConstantInfo("setting_opaque_public_chat_colour", count++, PrimitiveType.INT.INSTANCE, 87));
        constants.add(new ConstantInfo("setting_transparent_public_chat_colour", count++, PrimitiveType.INT.INSTANCE, 88));
        constants.add(new ConstantInfo("setting_opaque_private_chat_colour", count++, PrimitiveType.INT.INSTANCE, 89));
        constants.add(new ConstantInfo("setting_transparent_private_chat_colour", count++, PrimitiveType.INT.INSTANCE, 90));
        constants.add(new ConstantInfo("setting_split_private_chat_colour", count++, PrimitiveType.INT.INSTANCE, 91));
        constants.add(new ConstantInfo("setting_opaque_auto_chat_colour", count++, PrimitiveType.INT.INSTANCE, 92));
        constants.add(new ConstantInfo("setting_transparent_auto_chat_colour", count++, PrimitiveType.INT.INSTANCE, 93));
        constants.add(new ConstantInfo("setting_opaque_broadcast_colour", count++, PrimitiveType.INT.INSTANCE, 94));
        constants.add(new ConstantInfo("setting_transparent_broadcast_colour", count++, PrimitiveType.INT.INSTANCE, 95));
        constants.add(new ConstantInfo("setting_split_broadcast_colour", count++, PrimitiveType.INT.INSTANCE, 96));
        constants.add(new ConstantInfo("setting_opaque_friend_chat_colour", count++, PrimitiveType.INT.INSTANCE, 97));
        constants.add(new ConstantInfo("setting_transparent_friend_chat_colour", count++, PrimitiveType.INT.INSTANCE, 98));
        constants.add(new ConstantInfo("setting_opaque_clan_chat_colour", count++, PrimitiveType.INT.INSTANCE, 99));
        constants.add(new ConstantInfo("setting_transparent_clan_chat_colour", count++, PrimitiveType.INT.INSTANCE, 100));
        constants.add(new ConstantInfo("setting_opaque_incoming_trade_request_colour", count++, PrimitiveType.INT.INSTANCE, 101));
        constants.add(new ConstantInfo("setting_transparent_incoming_trade_request_colour", count++, PrimitiveType.INT.INSTANCE, 102));
        constants.add(new ConstantInfo("setting_opaque_incoming_challenge_request", count++, PrimitiveType.INT.INSTANCE, 103));
        constants.add(new ConstantInfo("setting_transparent_incoming_challenge_request_colour", count++, PrimitiveType.INT.INSTANCE, 104));
        constants.add(new ConstantInfo("setting_opaque_guest_clan_chat_colour", count++, PrimitiveType.INT.INSTANCE, 105));
        constants.add(new ConstantInfo("setting_transparent_guest_clan_chat_colour", count++, PrimitiveType.INT.INSTANCE, 106));
        constants.add(new ConstantInfo("setting_standard_health_overlay", count++, PrimitiveType.INT.INSTANCE, 111));
        constants.add(new ConstantInfo("setting_tile_highlight_colour_colour", count++, PrimitiveType.INT.INSTANCE, 113));
        constants.add(new ConstantInfo("setting_mouseover_tooltips", count++, PrimitiveType.INT.INSTANCE, 114));
        constants.add(new ConstantInfo("setting_antidrag", count++, PrimitiveType.INT.INSTANCE, 115));
        constants.add(new ConstantInfo("setting_regen_indicator", count++, PrimitiveType.INT.INSTANCE, 116));
        constants.add(new ConstantInfo("setting_helper_cox", count++, PrimitiveType.INT.INSTANCE, 118));
        constants.add(new ConstantInfo("setting_attack_style", count++, PrimitiveType.INT.INSTANCE, 123));
        constants.add(new ConstantInfo("setting_buff_home_teleport", count++, PrimitiveType.INT.INSTANCE, 125));
        constants.add(new ConstantInfo("setting_buff_minigame_teleport", count++, PrimitiveType.INT.INSTANCE, 126));
        constants.add(new ConstantInfo("setting_buff_bar", count++, PrimitiveType.INT.INSTANCE, 124));
        constants.add(new ConstantInfo("setting_buff_leagues", count++, PrimitiveType.INT.INSTANCE, 127));
        constants.add(new ConstantInfo("setting_buff_bar_tooltip", count++, PrimitiveType.INT.INSTANCE, 128));
        constants.add(new ConstantInfo("setting_buff_teleblock", count++, PrimitiveType.INT.INSTANCE, 129));
        constants.add(new ConstantInfo("setting_buff_charge_spell", count++, PrimitiveType.INT.INSTANCE, 130));
        constants.add(new ConstantInfo("setting_buff_godwars_altar", count++, PrimitiveType.INT.INSTANCE, 131));
        constants.add(new ConstantInfo("setting_buff_dragonfire_shield_cooldown", count++, PrimitiveType.INT.INSTANCE, 132));
        constants.add(new ConstantInfo("setting_buff_imbued_heart_cooldown", count++, PrimitiveType.INT.INSTANCE, 133));
        constants.add(new ConstantInfo("setting_buff_vengeance_cooldown", count++, PrimitiveType.INT.INSTANCE, 134));
        constants.add(new ConstantInfo("setting_buff_vengeance_active", count++, PrimitiveType.INT.INSTANCE, 135));
        constants.add(new ConstantInfo("setting_buff_stamina_duration", count++, PrimitiveType.INT.INSTANCE, 136));
        constants.add(new ConstantInfo("setting_buff_prayer_enhance_duration", count++, PrimitiveType.INT.INSTANCE, 137));
        constants.add(new ConstantInfo("setting_buff_overload_duration", count++, PrimitiveType.INT.INSTANCE, 138));
        constants.add(new ConstantInfo("setting_buff_magic_imbue_duration", count++, PrimitiveType.INT.INSTANCE, 139));
        constants.add(new ConstantInfo("setting_buff_sire_stun_duration", count++, PrimitiveType.INT.INSTANCE, 140));
        constants.add(new ConstantInfo("setting_buff_freeze_duration", count++, PrimitiveType.INT.INSTANCE, 141));
        constants.add(new ConstantInfo("setting_buff_sotd_duration", count++, PrimitiveType.INT.INSTANCE, 142));
        constants.add(new ConstantInfo("setting_buff_divine_potion_duration", count++, PrimitiveType.INT.INSTANCE, 143));
        constants.add(new ConstantInfo("setting_buff_antifire_duration", count++, PrimitiveType.INT.INSTANCE, 144));
        constants.add(new ConstantInfo("setting_buff_antipoison_duration", count++, PrimitiveType.INT.INSTANCE, 145));
        constants.add(new ConstantInfo("setting_always_on_top", count++, PrimitiveType.INT.INSTANCE, 146));
        constants.add(new ConstantInfo("setting_mouseover_text", count++, PrimitiveType.INT.INSTANCE, 147));
        constants.add(new ConstantInfo("setting_buff_corruption", count++, PrimitiveType.INT.INSTANCE, 152));
        constants.add(new ConstantInfo("setting_buff_mark_of_darkness", count++, PrimitiveType.INT.INSTANCE, 153));
        constants.add(new ConstantInfo("setting_buff_shadow_veil", count++, PrimitiveType.INT.INSTANCE, 154));
        constants.add(new ConstantInfo("setting_buff_death_charge", count++, PrimitiveType.INT.INSTANCE, 155));
        constants.add(new ConstantInfo("setting_buff_ward_of_arceuus", count++, PrimitiveType.INT.INSTANCE, 156));
        constants.add(new ConstantInfo("setting_buff_resurrection", count++, PrimitiveType.INT.INSTANCE, 157));
        constants.add(new ConstantInfo("setting_helper_agility", count++, PrimitiveType.INT.INSTANCE, 163));
        constants.add(new ConstantInfo("setting_helper_agility_highlight_obstacles", count++, PrimitiveType.INT.INSTANCE, 164));
        constants.add(new ConstantInfo("setting_minimap_lock", count++, PrimitiveType.INT.INSTANCE, 166));
        constants.add(new ConstantInfo("setting_draw_distance", count++, PrimitiveType.INT.INSTANCE, 168));
        constants.add(new ConstantInfo("setting_interface_scaling_mode", count++, PrimitiveType.INT.INSTANCE, 169));
        constants.add(new ConstantInfo("setting_buff_ammo", count++, PrimitiveType.INT.INSTANCE, 170));
        constants.add(new ConstantInfo("setting_highlight_hovered_tile_colour_colour", count++, PrimitiveType.INT.INSTANCE, 174));
        constants.add(new ConstantInfo("setting_highlight_current_tile_colour_colour", count++, PrimitiveType.INT.INSTANCE, 177));
        constants.add(new ConstantInfo("setting_highlight_destination_tile_colour_colour", count++, PrimitiveType.INT.INSTANCE, 180));
        constants.add(new ConstantInfo("setting_store_button_desktop", count++, PrimitiveType.INT.INSTANCE, 181));
        constants.add(new ConstantInfo("setting_helper_slayer", count++, PrimitiveType.INT.INSTANCE, 184));
        constants.add(new ConstantInfo("setting_logout_notifier", count++, PrimitiveType.INT.INSTANCE, 185));
        constants.add(new ConstantInfo("setting_buff_poison_damage", count++, PrimitiveType.INT.INSTANCE, 186));
        constants.add(new ConstantInfo("setting_ore_respawn_timer", count++, PrimitiveType.INT.INSTANCE, 187));
        constants.add(new ConstantInfo("setting_woodcutting_respawn_timer", count++, PrimitiveType.INT.INSTANCE, 188));
        constants.add(new ConstantInfo("setting_bird_nest_notification", count++, PrimitiveType.INT.INSTANCE, 189));
        constants.add(new ConstantInfo("setting_popout_xptracker", count++, PrimitiveType.INT.INSTANCE, 191));
        constants.add(new ConstantInfo("setting_gravestone_confirmation", count++, PrimitiveType.INT.INSTANCE, 192));
        constants.add(new ConstantInfo("setting_opaque_iron_group_chat_colour", count++, PrimitiveType.INT.INSTANCE, 193));
        constants.add(new ConstantInfo("setting_transparent_iron_group_chat_colour", count++, PrimitiveType.INT.INSTANCE, 194));
        constants.add(new ConstantInfo("setting_opaque_clan_broadcasts_colour", count++, PrimitiveType.INT.INSTANCE, 196));
        constants.add(new ConstantInfo("setting_transparent_clan_broadcasts_colour", count++, PrimitiveType.INT.INSTANCE, 197));
        constants.add(new ConstantInfo("setting_opaque_iron_group_broadcasts_colour", count++, PrimitiveType.INT.INSTANCE, 198));
        constants.add(new ConstantInfo("setting_transparent_iron_group_broadcasts_colour", count++, PrimitiveType.INT.INSTANCE, 199));
        constants.add(new ConstantInfo("setting_buff_cannon_ammo", count++, PrimitiveType.INT.INSTANCE, 213));
        constants.add(new ConstantInfo("setting_unstarted_quest_text_colour_colour", count++, PrimitiveType.INT.INSTANCE, 224));
        constants.add(new ConstantInfo("setting_inprogress_quest_text_colour_colour", count++, PrimitiveType.INT.INSTANCE, 225));
        constants.add(new ConstantInfo("setting_completed_quest_text_colour_colour", count++, PrimitiveType.INT.INSTANCE, 226));
        constants.add(new ConstantInfo("setting_unavailable_quest_text_colour_colour", count++, PrimitiveType.INT.INSTANCE, 227));
        constants.add(new ConstantInfo("setting_renderer", count++, PrimitiveType.INT.INSTANCE, 600));
        constants.add(new ConstantInfo("setting_max_fps", count++, PrimitiveType.INT.INSTANCE, 601));
        constants.add(new ConstantInfo("setting_hardshadows", count++, PrimitiveType.INT.INSTANCE, 602));
        constants.add(new ConstantInfo("setting_skybox", count++, PrimitiveType.INT.INSTANCE, 603));
        constants.add(new ConstantInfo("setting_textures", count++, PrimitiveType.INT.INSTANCE, 604));
        constants.add(new ConstantInfo("setting_bloom", count++, PrimitiveType.INT.INSTANCE, 605));
        constants.add(new ConstantInfo("setting_particles", count++, PrimitiveType.INT.INSTANCE, 606));
        constants.add(new ConstantInfo("setting_waterdetail", count++, PrimitiveType.INT.INSTANCE, 607));
        constants.add(new ConstantInfo("setting_flickeringlights", count++, PrimitiveType.INT.INSTANCE, 608));
        constants.add(new ConstantInfo("setting_lightdetail", count++, PrimitiveType.INT.INSTANCE, 609));
        constants.add(new ConstantInfo("setting_removeroofs", count++, PrimitiveType.INT.INSTANCE, 610));
        constants.add(new ConstantInfo("setting_groundecor", count++, PrimitiveType.INT.INSTANCE, 611));
        constants.add(new ConstantInfo("setting_forcehd", count++, PrimitiveType.INT.INSTANCE, 612));
        constants.add(new ConstantInfo("setting_animdetail", count++, PrimitiveType.INT.INSTANCE, 613));
        constants.add(new ConstantInfo("setting_antialiasing", count++, PrimitiveType.INT.INSTANCE, 614));
        constants.add(new ConstantInfo("setting_fog", count++, PrimitiveType.INT.INSTANCE, 615));
        constants.add(new ConstantInfo("setting_battlepets_autoattack", count++, PrimitiveType.INT.INSTANCE, 616));
        constants.add(new ConstantInfo("setting_loadingscreens", count++, PrimitiveType.INT.INSTANCE, 617));
        constants.add(new ConstantInfo("pref_renderer", count++, PrimitiveType.INT.INSTANCE, 50));
        constants.add(new ConstantInfo("pref_max_fps", count++, PrimitiveType.INT.INSTANCE, 51));
        constants.add(new ConstantInfo("pref_hardshadows", count++, PrimitiveType.INT.INSTANCE, 52));
        constants.add(new ConstantInfo("pref_skybox", count++, PrimitiveType.INT.INSTANCE, 53));
        constants.add(new ConstantInfo("pref_textures", count++, PrimitiveType.INT.INSTANCE, 54));
        constants.add(new ConstantInfo("pref_bloom", count++, PrimitiveType.INT.INSTANCE, 55));
        constants.add(new ConstantInfo("pref_particles", count++, PrimitiveType.INT.INSTANCE, 56));
        constants.add(new ConstantInfo("pref_waterdetail", count++, PrimitiveType.INT.INSTANCE, 57));
        constants.add(new ConstantInfo("pref_flickeringlights", count++, PrimitiveType.INT.INSTANCE, 58));
        constants.add(new ConstantInfo("pref_lightdetail", count++, PrimitiveType.INT.INSTANCE, 59));
        constants.add(new ConstantInfo("pref_removeroofs", count++, PrimitiveType.INT.INSTANCE, 60));
        constants.add(new ConstantInfo("pref_groundecor", count++, PrimitiveType.INT.INSTANCE, 61));
        constants.add(new ConstantInfo("pref_forcehd", count++, PrimitiveType.INT.INSTANCE, 62));
        constants.add(new ConstantInfo("pref_animdetail", count++, PrimitiveType.INT.INSTANCE, 63));
        constants.add(new ConstantInfo("pref_antialiasing", count++, PrimitiveType.INT.INSTANCE, 64));
        constants.add(new ConstantInfo("pref_fog", count++, PrimitiveType.INT.INSTANCE, 65));


    }

    public void read(PrimitiveType type, Path file) throws IOException {
        var loader = ConfigSymbolLoader.INSTANCE;
        Files.lines(file).forEach(line -> defineConfig(type, loader.load(line)));
    }

    /**
     * Defines a new constant symbol in this table.
     *
     * @param name  the name of the constant.
     * @param type  the type of the constant.f
     * @param value the value of the constant.
     */
    public void defineConstant(String name, int id, Type type, Object value) {
        if (lookupConstant(name) != null) {
            throw new IllegalArgumentException("The constant '" + name + "' is already defined.");
        }
        constants.add(new ConstantInfo(name, id, type, value));
    }

    /**
     * Looks-up for the {@link ConstantInfo constant information} with the specified {@code name}.
     *
     * @param name the name of the constant.
     * @return the {@link ConstantInfo} if it was present otherwise {@code null}.
     */
    public ConstantInfo lookupConstant(String name) {
        var info = constants.lookupByName(name);
        if (info == null && parent != null) {
            info = parent.lookupConstant(name);
        }
        return info;
    }

    /**
     * Defines the specified {@link ConfigSymbol} in the symbol table.
     *
     * @param info the configuration info object to define.
     */
    public void defineConfig(PrimitiveType type, ConfigSymbol info) {
        if (lookupConfig(type, info.getName()) != null) {
            throw new IllegalArgumentException("The configuration '" + info.getName() + "' is already defined.");
        }
        var list = configs.get(type);
        if (list == null) {
            list = new SymbolList<>();
            configs.put(type, list);
        }
        list.add(info);
    }

    /**
     * Looks-up for the {@link ConfigSymbol configuration information} with the specified {@code name}.
     *
     * @param name the name of the configuration type value.
     * @return the {@link ConfigSymbol} if it was present otherwise {@code null}.
     */
    public ConfigSymbol lookupConfig(Type type, String name) {
        var list = configs.get(type);
        ConfigSymbol info = null;
        if (list != null) {
            info = list.lookupByName(name);
        }
        if (info == null && parent != null) {
            info = parent.lookupConfig(type, name);
        }
        return info;
    }


    /**
     * Looks-up for the {@link ConfigSymbol variable information} with the specified {@code name}.
     *
     * @param name the name of the variable configuration type value.
     * @return the {@link ConfigSymbol} if it was present otherwise {@code null}.
     */
    public ConfigSymbol lookupVariable(String name) {
        var varConfig = lookupConfig(PrimitiveType.VARP.INSTANCE, name);
        if (varConfig != null) {
            return varConfig;
        }
        var varBitConfig = lookupConfig(PrimitiveType.VARBIT.INSTANCE, name);
        if (varBitConfig != null) {
            return varBitConfig;
        }
        var varcConfig = lookupConfig(PrimitiveType.VARC.INSTANCE, name);
        if (varcConfig != null) {
            return varcConfig;
        }
        return null;
    }

    public PrimitiveType lookupVariableDomain(String name) {
        var varConfig = lookupConfig(PrimitiveType.VARP.INSTANCE, name);
        if (varConfig != null) {
            return PrimitiveType.VARP.INSTANCE;
        }
        var varBitConfig = lookupConfig(PrimitiveType.VARBIT.INSTANCE, name);
        if (varBitConfig != null) {
            return PrimitiveType.VARBIT.INSTANCE;
        }
        var varcConfig = lookupConfig(PrimitiveType.VARC.INSTANCE, name);
        if (varcConfig != null) {
            return PrimitiveType.VARC.INSTANCE;
        }
        return null;
    }

    /**
     * Defines a new runtime constant symbol in this table.
     *
     * @param name  the name of the runtime constant.
     * @param type  the type of the runtime constant.
     * @param value the value of the runtime constant.
     */
    public void defineRuntimeConstant(String name, int id, PrimitiveType type, Object value) {
        if (lookupRuntimeConstant(name) != null) {
            throw new IllegalArgumentException("The runtime constant '" + name + "' is already defined.");
        }
        runtimeConstants.add(new RuntimeConstantInfo(name, id, type, value));
    }

    /**
     * Looks-up for the {@link RuntimeConstantInfo} with the specified {@code name}.
     *
     * @param name the name of the runtime constant.
     * @return the {@link RuntimeConstantInfo} if it was present otherwise {@code null}.
     */
    public RuntimeConstantInfo lookupRuntimeConstant(String name) {
        var info = runtimeConstants.lookupByName(name);
        if (info == null && parent != null) {
            info = parent.lookupRuntimeConstant(name);
        }
        return info;
    }

    /**
     * Creates a nested sub symbol table.
     *
     * @return the created {@link SymbolTable} object.
     */
    public SymbolTable createSubTable() {
        return new SymbolTable(this, true);
    }
}
